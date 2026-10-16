import { readFileSync } from "node:fs";
import { initializeTestEnvironment, assertSucceeds, assertFails } from "@firebase/rules-unit-testing";
import { doc, setDoc, getDoc, serverTimestamp, updateDoc } from "firebase/firestore";

const env = await initializeTestEnvironment({
  projectId: "kemistri-rules-test",
  firestore: { rules: readFileSync("../firestore.rules", "utf8"), host: "127.0.0.1", port: 8089 },
});
const today = new Date().toISOString().slice(0, 10);
const old = new Date(Date.now() - 5 * 864e5).toISOString().slice(0, 10);
const future = new Date(Date.now() + 2 * 864e5).toISOString().slice(0, 10);
const alice = env.authenticatedContext("alice").firestore();
const bob = env.authenticatedContext("bob").firestore();
const anon = env.unauthenticatedContext().firestore();
const profile = (name) => ({ displayName: name, normalizedName: name.toLowerCase(), updatedAt: serverTimestamp() });
const score = (o = {}) => ({
  displayName: "SwiftFlask414", score: 1150, moleculeScore: 750, scienceScore: 400, uniqueDiscoveries: 10,
  mysteriesSolved: 5, contentVersion: 1, submittedAt: serverTimestamp(), verified: false, ...o,
});
const at = (db, date, uid) => doc(db, "daily_champions", date, "players", uid);
let passed = 0, failed = 0;
async function check(name, p) { try { await p; passed++; console.log("PASS", name); } catch (e) { failed++; console.log("FAIL", name, e.message); } }

await check("profile create", assertSucceeds(setDoc(doc(alice, "players", "alice"), profile("SwiftFlask414"))));
await check("cannot write someone else's profile", assertFails(setDoc(doc(bob, "players", "alice"), profile("Hacker"))));
await check("cannot read someone else's profile", assertFails(getDoc(doc(bob, "players", "alice"))));
await check("signed-out cannot read scores", assertFails(getDoc(at(anon, today, "alice"))));
await check("wrong total rejected", assertFails(setDoc(at(alice, today, "alice"), score({ score: 99999 }))));
await check("molecule over cap rejected", assertFails(setDoc(at(alice, today, "alice"), score({ moleculeScore: 2700, uniqueDiscoveries: 20, score: 3100 }))));
await check("empty board with points rejected", assertFails(setDoc(at(alice, today, "alice"), score({ moleculeScore: 50, uniqueDiscoveries: 0, score: 450 }))));
await check("science over cap rejected", assertFails(setDoc(at(alice, today, "alice"), score({ scienceScore: 600, score: 1350 }))));
await check("verified=true rejected", assertFails(setDoc(at(alice, today, "alice"), score({ verified: true }))));
await check("extra field rejected", assertFails(setDoc(at(alice, today, "alice"), score({ cheat: 1 }))));
await check("fake name rejected", assertFails(setDoc(at(alice, today, "alice"), score({ displayName: "TopPlayer" }))));
await check("old date rejected", assertFails(setDoc(at(alice, old, "alice"), score())));
await check("future date rejected", assertFails(setDoc(at(alice, future, "alice"), score())));
await check("bad date rejected", assertFails(setDoc(at(alice, "2026-1-1", "alice"), score())));
await check("writing as another player rejected", assertFails(setDoc(at(bob, today, "alice"), score())));
await check("valid score accepted", assertSucceeds(setDoc(at(alice, today, "alice"), score())));
await check("second write rejected", assertFails(setDoc(at(alice, today, "alice"), score({ moleculeScore: 760, score: 1160 }))));
await check("update rejected", assertFails(updateDoc(at(alice, today, "alice"), { score: 2000 })));
await check("signed-in can read board", assertSucceeds(getDoc(at(bob, today, "alice"))));
await check("old collection closed", assertFails(setDoc(doc(alice, "science_daily_scores", today, "players", "alice"), { score: 1 })));
await env.cleanup();
console.log(`${passed} passed, ${failed} failed`);
process.exit(failed ? 1 : 0);
