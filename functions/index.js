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
  const profileRef = db.collection("players").doc(uid);
  const scoreRef = db.collection("daily_champions").doc(date).collection("players").doc(uid);

  return db.runTransaction(async (transaction) => {
    const existing = await transaction.get(scoreRef);
    if (existing.exists) return { alreadySubmitted: true, rank: null };

    const profile = await transaction.get(profileRef);
    const displayName = profile.exists && typeof profile.data().displayName === "string"
      ? profile.data().displayName
      : "Mystery Scientist";

    transaction.create(scoreRef, {
      displayName,
      score: moleculeScore + scienceScore,
      moleculeScore,
      scienceScore,
      uniqueDiscoveries,
      mysteriesSolved,
      contentVersion,
      submittedAt: FieldValue.serverTimestamp(),
      verified: true,
    });
    return { alreadySubmitted: false, rank: null };
  });
});
