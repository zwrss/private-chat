function getKey() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            var response = JSON.parse(this.responseText);
            document.getElementById("publicKey").value = response.publicKey;
            document.getElementById("privateKey").value = response.privateKey;
            document.getElementById("modulus").value = response.modulus;
        }
    };
    xhttp.open("GET", "key?strength=" + document.getElementById("strength").value, true);
    xhttp.send();
}

function getEncrypted() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            console.log(this.responseText);
            document.getElementById("message").value = this.responseText;
        }
    };
    var cipher = 'ceasar';
    if (document.getElementById("cipher_rsa").checked) cipher = 'rsa'
    if (document.getElementById("cipher_base64").checked) cipher = 'base64'
    var publicKey = document.getElementById("publicKey").value;
    var modulus = document.getElementById("modulus").value;
    var message = document.getElementById("message").value;
    var shift = document.getElementById("shift").value;
    var base64 = document.getElementById("base64").checked;
    xhttp.open("POST", "encrypt", true);
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhttp.send(
        "message=" + encodeURIComponent(message) +
        "&cipher=" + encodeURIComponent(cipher) +
        "&publicKey=" + encodeURIComponent(publicKey) +
        "&modulus=" + encodeURIComponent(modulus) +
        "&shift=" + encodeURIComponent(shift) +
        "&base64=" + encodeURIComponent(base64)
    );
}

function getDecrypted() {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4 && this.status == 200) {
            console.log(this.responseText);
            document.getElementById("message").value = this.responseText;
        }
    };
    var cipher = 'ceasar';
    if (document.getElementById("cipher_rsa").checked) cipher = 'rsa'
    if (document.getElementById("cipher_base64").checked) cipher = 'base64'
    var privateKey = document.getElementById("privateKey").value
    var modulus = document.getElementById("modulus").value;
    var message = document.getElementById("message").value;
    var shift = document.getElementById("shift").value;
    var base64 = document.getElementById("base64").checked;
    xhttp.open("POST", "decrypt", true);
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhttp.send(
        "message=" + encodeURIComponent(message) +
        "&cipher=" + encodeURIComponent(cipher) +
        "&privateKey=" + encodeURIComponent(privateKey) +
        "&modulus=" + encodeURIComponent(modulus) +
        "&shift=" + encodeURIComponent(shift) +
        "&base64=" + encodeURIComponent(base64)
    );
}


function getEncryptedJS() {
    var cipher = 'ceasar';
    if (document.getElementById("cipher_rsa").checked) cipher = 'rsa'
    if (document.getElementById("cipher_base64").checked) cipher = 'base64'
    var publicKey = document.getElementById("publicKey").value;
    var modulus = document.getElementById("modulus").value;
    var message = document.getElementById("message").value;
    var shift = document.getElementById("shift").value;
    var base64 = document.getElementById("base64").checked;
    if (cipher === 'ceasar') {
        document.getElementById("message").value = CeasarCipher.encrypt(message, shift, base64);
    } else if (cipher === 'base64') {
        document.getElementById("message").value = B64.encode(message);
    } else {
        document.getElementById("message").value = RSA.encrypt(message, modulus, publicKey);
    }
}

function getDecryptedJS() {
    var cipher = 'ceasar';
    if (document.getElementById("cipher_rsa").checked) cipher = 'rsa'
    if (document.getElementById("cipher_base64").checked) cipher = 'base64'
    var privateKey = document.getElementById("privateKey").value;
    var modulus = document.getElementById("modulus").value;
    var message = document.getElementById("message").value;
    var shift = document.getElementById("shift").value;
    var base64 = document.getElementById("base64").checked;
    if (cipher === 'ceasar') {
        document.getElementById("message").value = CeasarCipher.decrypt(message, shift, base64);
    } else if (cipher === 'base64') {
        document.getElementById("message").value = B64.decode(message);
    } else {
        document.getElementById("message").value = RSA.decrypt(message, modulus, privateKey);
    }
}

function getKeyJS() {

    var strength = parseInt(document.getElementById("strength").value);

    var rsa = RSA.generateKeys(strength);

    document.getElementById("publicKey").value = rsa.publicKey;
    document.getElementById("privateKey").value = rsa.privateKey;
    document.getElementById("modulus").value = rsa.modulus;
}

function init() {
    document.getElementById("actionKey").onclick = getKey;
    document.getElementById("actionEncrypt").onclick = getEncrypted;
    document.getElementById("actionDecrypt").onclick = getDecrypted;


    document.getElementById("actionKeyJS").onclick = getKeyJS;
    document.getElementById("actionEncryptJS").onclick = getEncryptedJS;
    document.getElementById("actionDecryptJS").onclick = getDecryptedJS;

}

window.addEventListener("load", init, false);