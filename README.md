# Lenvora V2 — Real Offline OCR + Translation Engine

This module adds the real on-device engine using Google ML Kit.

- OCR: bundled Latin text recognition, works without network after installation.
- Translation: on-device ML Kit translation models. Models are downloaded once and then translation can work offline.
- Language identification: detects the source language before translation.
- Camera: CameraX preview/capture foundation.
- Supported target/source examples include Persian, English, Arabic, Turkish, German, French and Spanish.

Important: ML Kit translation models are downloaded on demand. The first model download needs connectivity; after the model is installed, translation runs on-device. Google documents models at about 30 MB each and recommends downloading over Wi‑Fi. citeturn0search1

OCR accuracy depends on image quality, focus and resolution. citeturn0search6
