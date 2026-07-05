const {setGlobalOptions} = require("firebase-functions");
const {onRequest} = require("firebase-functions/https");

setGlobalOptions({ maxInstances: 10 });

const admin = require("firebase-admin");
const { getStorage } = require("firebase-admin/storage");
const logger = require("firebase-functions/logger");
const { onObjectFinalized } = require("firebase-functions/v2/storage");
const csvtojsonV2 = require("csvtojson/v2");

const isPowerOfTen = (num) => {
    if (num <= 0) return false;
    return Number.isInteger(Math.log10(num));
};

admin.initializeApp();
exports.importSTBData = onObjectFinalized({ maxInstances: 1 }, async (event) => {
    logger.info("processing file", { structuredData: true });
    const db = admin.firestore();

    const fileBucket = event.data.bucket; // Storage bucket containing the file.
    const filePath = event.data.name;
    const extension = filePath.split(".")[1];
    const name = filePath.split(".")[0];

    if(extension === "json" || extension === "csv") {

        const bucket = getStorage().bucket(fileBucket);

        // Download file to memory
        let downloadResponse = await bucket.file(filePath).download();
        let json_file, collection;

        if(extension === "csv") {
            if(name.includes("stb-stops")) {
                collection = "stops"
                json_file = await csvtojsonV2({
                    colParser: {
                        "id": "number",
                        "name": "string",
                        "description": "string",
                        "latitude": "number",
                        "longitude": "number",
                        "type": "number"
                    },
                    checkType: true
                })
                    .fromString(downloadResponse.toString())
            } else console.log("CSV file is not recognized!!")
        } else {
            collection = name;
            json_file = JSON.parse(downloadResponse.toString());
        }

        // Get batch client
        const batch = db.batch();

        var char = 'A'
        // Perform updates
        json_file.forEach((todo, ind) => {
            if(isPowerOfTen(ind))
                char = String.fromCharCode(char.charCodeAt(0) + 1);
            const ref = db.doc(`${collection}/${char + ind}`);
            batch.set(ref, todo);
        });

        // Commit updates
        await batch.commit();

        //Update timestamp
        const property = collection + "LastUpdated";
        await db.collection("metadata").doc("list_updates").set({
            [property]: Date.now()
        }, { merge: true });
    } else console.log("Uploaded file is not JSON or CSV!!");
});