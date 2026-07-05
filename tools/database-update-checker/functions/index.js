const {setGlobalOptions} = require("firebase-functions");
const {onRequest, onCall} = require("firebase-functions/https");
const logger = require("firebase-functions/logger");
const {getFirestore} = require("firebase-admin/firestore");
const {initializeApp} = require("firebase-admin/app");
const {onDocumentWritten} = require("firebase-functions/firestore");

setGlobalOptions({ maxInstances: 10 });

initializeApp();
const db = getFirestore();

exports.checkDatabaseUpdates = onCall(
    { region: "europe-central2" },
    async (request) => {
        const lastUpdatedClient = request.data.lastUpdated;
        const property = request.data.collection + "LastUpdated";
        const doc = await db.collection("metadata").doc("list_updates").get()
        const lastUpdatedServer = doc.data()[property];

        return lastUpdatedClient < lastUpdatedServer;
});