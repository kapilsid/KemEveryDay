"use strict";

const { initializeApp } = require("firebase-admin/app");
const { getFirestore, FieldValue } = require("firebase-admin/firestore");
const { onCall, HttpsError } = require("firebase-functions/v2/https");
const { setGlobalOptions } = require("firebase-functions/v2/options");

initializeApp();
setGlobalOptions({ region: "europe-west2", maxInstances: 10 });

const db = getFirestore();

// Optional (Blaze plan): server-side submission that marks Daily Champions scores as verified.
// Limits mirror ChampionLimits in the app and firestore.rules.
exports.submitDailyChampions = onCall({ enforceAppCheck: false }, async (request) => {
  if (!request.auth) {
    throw new HttpsError("unauthenticated", "A player identity is required.");
  }

  const { date, moleculeScore, scienceScore, uniqueDiscoveries, mysteriesSolved, contentVersion } = request.data || {};
  const todayUtc = new Date().toISOString().slice(0, 10);
  if (typeof date !== "string" || !/^\d{4}-\d{2}-\d{2}$/.test(date) || date !== todayUtc) {
    throw new HttpsError("invalid-argument", "Only today's UTC scores can be submitted.");
  }
  const isCount = (value, max) => Number.isInteger(value) && value >= 0 && value <= max;
  if (!isCount(uniqueDiscoveries, 20) || !isCount(mysteriesSolved, 3) ||
      !isCount(moleculeScore, uniqueDiscoveries === 0 ? 0 : uniqueDiscoveries * 130 + (20 - uniqueDiscoveries) * 30) ||
      !isCount(scienceScore, mysteriesSolved * 175) ||
      !Number.isInteger(contentVersion) || contentVersion < 1) {
    throw new HttpsError("invalid-argument", "The score is outside the valid range.");
  }

  const uid = request.auth.uid;
  const dateValue = new Date(`${date}T00:00:00Z`);
  dateValue.setUTCDate(dateValue.getUTCDate() - ((dateValue.getUTCDay() + 6) % 7));
  const weekStart = dateValue.toISOString().slice(0, 10);
  const profileRef = db.collection("players").doc(uid);
  const scoreRef = db.collection("daily_champions").doc(date).collection("players").doc(uid);
  const weeklyRef = db.collection("weekly_champions").doc(weekStart).collection("players").doc(uid);

  return db.runTransaction(async (transaction) => {
    const existing = await transaction.get(scoreRef);
    const profile = await transaction.get(profileRef);
    const displayName = profile.exists && typeof profile.data().displayName === "string"
      ? profile.data().displayName
      : "Mystery Scientist";

    const previous = existing.exists ? existing.data() : {};
    const nextMoleculeScore = Math.max(previous.moleculeScore || 0, moleculeScore);
    const nextScienceScore = Math.max(previous.scienceScore || 0, scienceScore);
    const nextUniqueDiscoveries = Math.max(previous.uniqueDiscoveries || 0, uniqueDiscoveries);
    const nextMysteriesSolved = Math.max(previous.mysteriesSolved || 0, mysteriesSolved);
    const nextScore = nextMoleculeScore + nextScienceScore;
    if (existing.exists && previous.score >= nextScore && previous.verified === true) {
      return { alreadySubmitted: true, rank: null };
    }

    const weekly = await transaction.get(weeklyRef);
    const weeklyData = weekly.exists ? weekly.data() : {};
    const weeklyMoleculeScore = (weeklyData.moleculeScore || 0) + nextMoleculeScore - (previous.moleculeScore || 0);
    const weeklyScienceScore = (weeklyData.scienceScore || 0) + nextScienceScore - (previous.scienceScore || 0);
    const weeklyUniqueDiscoveries = (weeklyData.uniqueDiscoveries || 0) + nextUniqueDiscoveries - (previous.uniqueDiscoveries || 0);
    const weeklyMysteriesSolved = (weeklyData.mysteriesSolved || 0) + nextMysteriesSolved - (previous.mysteriesSolved || 0);
    const daysPlayed = (weeklyData.daysPlayed || 0) + (existing.exists ? 0 : 1);

    transaction.set(scoreRef, {
      displayName,
      score: nextScore,
      moleculeScore: nextMoleculeScore,
      scienceScore: nextScienceScore,
      uniqueDiscoveries: nextUniqueDiscoveries,
      mysteriesSolved: nextMysteriesSolved,
      contentVersion: Math.max(previous.contentVersion || 1, contentVersion),
      submittedAt: FieldValue.serverTimestamp(),
      verified: true,
    });
    transaction.set(weeklyRef, {
      displayName,
      score: weeklyMoleculeScore + weeklyScienceScore,
      moleculeScore: weeklyMoleculeScore,
      scienceScore: weeklyScienceScore,
      daysPlayed,
      uniqueDiscoveries: weeklyUniqueDiscoveries,
      mysteriesSolved: weeklyMysteriesSolved,
      contentVersion: Math.max(weeklyData.contentVersion || 1, contentVersion),
      updatedAt: FieldValue.serverTimestamp(),
      verified: true,
    });
    return { alreadySubmitted: false, updated: existing.exists, rank: null };
  });
});
