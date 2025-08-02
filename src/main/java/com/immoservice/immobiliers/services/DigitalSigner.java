// package com.immoservice.immobiliers.services;

// import com.itextpdf.kernel.pdf.PdfReader;
// import com.itextpdf.kernel.pdf.StampingProperties;
// import com.itextpdf.signatures.*;
// import org.bouncycastle.jce.provider.BouncyCastleProvider;

// import java.io.FileInputStream;
// import java.io.FileOutputStream;
// import java.security.KeyStore;
// import java.security.PrivateKey;
// import java.security.Security;
// import java.security.cert.Certificate;

// public class DigitalSigner {

// public void signerNumeriquement(String src, String dest, String keystorePath,
// String password) throws Exception {
// // Ajoute BouncyCastle comme fournisseur de sécurité
// Security.addProvider(new BouncyCastleProvider());

// KeyStore ks = KeyStore.getInstance("PKCS12");
// ks.load(new FileInputStream(keystorePath), password.toCharArray());

// String alias = ks.aliases().nextElement();
// PrivateKey pk = (PrivateKey) ks.getKey(alias, password.toCharArray());
// Certificate[] chain = ks.getCertificateChain(alias);

// PdfReader reader = new PdfReader(src);
// PdfSigner signer = new PdfSigner(reader, new FileOutputStream(dest), new
// StampingProperties());

// PdfSignatureAppearance appearance = signer.getSignatureAppearance()
// .setReason("Signature contrat de vente")
// .setLocation("Cotonou")
// .setPageRect(new com.itextpdf.kernel.geom.Rectangle(400, 100, 150, 100)) //
// position visible
// .setPageNumber(1)
// .setReuseAppearance(false);
// // .setCertificate(chain[0]); // ce n'est pas nécessaire ici

// signer.setFieldName("signature");

// IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256,
// BouncyCastleProvider.PROVIDER_NAME);
// IExternalDigest digest = new BouncyCastleDigest();

// signer.signDetached(digest, pks, chain, null, null, null, 0,
// PdfSigner.CryptoStandard.CADES);
// }
// }
