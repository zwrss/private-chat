class B64 {

    static b64ToUint6(nChr) {

        return nChr > 64 && nChr < 91 ?
            nChr - 65 :
            nChr > 96 && nChr < 123 ?
            nChr - 71 :
            nChr > 47 && nChr < 58 ?
            nChr + 4 :
            nChr === 43 ?
            62 :
            nChr === 47 ?
            63 :
            0;

    }

    static base64DecToArr(sBase64, nBlockSize) {

        var
            sB64Enc = sBase64.replace(/[^A-Za-z0-9\+\/]/g, ""),
            nInLen = sB64Enc.length,
            nOutLen = nBlockSize ? Math.ceil((nInLen * 3 + 1 >>> 2) / nBlockSize) * nBlockSize : nInLen * 3 + 1 >>> 2,
            aBytes = new Uint8Array(nOutLen);

        for (var nMod3, nMod4, nUint24 = 0, nOutIdx = 0, nInIdx = 0; nInIdx < nInLen; nInIdx++) {
            nMod4 = nInIdx & 3;
            nUint24 |= B64.b64ToUint6(sB64Enc.charCodeAt(nInIdx)) << 18 - 6 * nMod4;
            if (nMod4 === 3 || nInLen - nInIdx === 1) {
                for (nMod3 = 0; nMod3 < 3 && nOutIdx < nOutLen; nMod3++, nOutIdx++) {
                    aBytes[nOutIdx] = nUint24 >>> (16 >>> nMod3 & 24) & 255;
                }
                nUint24 = 0;
            }
        }

        return aBytes;
    }

    /* Base64 string to array encoding */

    static uint6ToB64(nUint6) {

        return nUint6 < 26 ?
            nUint6 + 65 :
            nUint6 < 52 ?
            nUint6 + 71 :
            nUint6 < 62 ?
            nUint6 - 4 :
            nUint6 === 62 ?
            43 :
            nUint6 === 63 ?
            47 :
            65;

    }

    static base64EncArr(aBytes) {

        var eqLen = (3 - (aBytes.length % 3)) % 3,
            sB64Enc = "";

        for (var nMod3, nLen = aBytes.length, nUint24 = 0, nIdx = 0; nIdx < nLen; nIdx++) {
            nMod3 = nIdx % 3;
            /* Uncomment the following line in order to split the output in lines 76-character long: */
            /*
            if (nIdx > 0 && (nIdx * 4 / 3) % 76 === 0) { sB64Enc += "\r\n"; }
            */
            nUint24 |= aBytes[nIdx] << (16 >>> nMod3 & 24);
            if (nMod3 === 2 || aBytes.length - nIdx === 1) {
                sB64Enc += String.fromCharCode(B64.uint6ToB64(nUint24 >>> 18 & 63), B64.uint6ToB64(nUint24 >>> 12 & 63), B64.uint6ToB64(nUint24 >>> 6 & 63), B64.uint6ToB64(nUint24 & 63));
                nUint24 = 0;
            }
        }

        return eqLen === 0 ?
            sB64Enc :
            sB64Enc.substring(0, sB64Enc.length - eqLen) + (eqLen === 1 ? "=" : "==");

    }

    static UTF8ArrToStr(aBytes) {

        var sView = "";

        for (var nPart, nLen = aBytes.length, nIdx = 0; nIdx < nLen; nIdx++) {
            nPart = aBytes[nIdx];
            sView += String.fromCharCode(
                nPart > 251 && nPart < 254 && nIdx + 5 < nLen ? /* six bytes */
                /* (nPart - 252 << 30) may be not so safe in ECMAScript! So...: */
                (nPart - 252) * 1073741824 + (aBytes[++nIdx] - 128 << 24) + (aBytes[++nIdx] - 128 << 18) + (aBytes[++nIdx] - 128 << 12) + (aBytes[++nIdx] - 128 << 6) + aBytes[++nIdx] - 128 :
                nPart > 247 && nPart < 252 && nIdx + 4 < nLen ? /* five bytes */
                (nPart - 248 << 24) + (aBytes[++nIdx] - 128 << 18) + (aBytes[++nIdx] - 128 << 12) + (aBytes[++nIdx] - 128 << 6) + aBytes[++nIdx] - 128 :
                nPart > 239 && nPart < 248 && nIdx + 3 < nLen ? /* four bytes */
                (nPart - 240 << 18) + (aBytes[++nIdx] - 128 << 12) + (aBytes[++nIdx] - 128 << 6) + aBytes[++nIdx] - 128 :
                nPart > 223 && nPart < 240 && nIdx + 2 < nLen ? /* three bytes */
                (nPart - 224 << 12) + (aBytes[++nIdx] - 128 << 6) + aBytes[++nIdx] - 128 :
                nPart > 191 && nPart < 224 && nIdx + 1 < nLen ? /* two bytes */
                (nPart - 192 << 6) + aBytes[++nIdx] - 128 :
                /* nPart < 127 ? */
                /* one byte */
                nPart
            );
        }

        return sView;

    }

    static strToUTF8Arr(sDOMStr) {

        var aBytes, nChr, nStrLen = sDOMStr.length,
            nArrLen = 0;

        /* mapping... */

        for (var nMapIdx = 0; nMapIdx < nStrLen; nMapIdx++) {
            nChr = sDOMStr.charCodeAt(nMapIdx);
            nArrLen += nChr < 0x80 ? 1 : nChr < 0x800 ? 2 : nChr < 0x10000 ? 3 : nChr < 0x200000 ? 4 : nChr < 0x4000000 ? 5 : 6;
        }

        aBytes = new Uint8Array(nArrLen);

        /* transcription... */

        for (var nIdx = 0, nChrIdx = 0; nIdx < nArrLen; nChrIdx++) {
            nChr = sDOMStr.charCodeAt(nChrIdx);
            if (nChr < 128) {
                /* one byte */
                aBytes[nIdx++] = nChr;
            } else if (nChr < 0x800) {
                /* two bytes */
                aBytes[nIdx++] = 192 + (nChr >>> 6);
                aBytes[nIdx++] = 128 + (nChr & 63);
            } else if (nChr < 0x10000) {
                /* three bytes */
                aBytes[nIdx++] = 224 + (nChr >>> 12);
                aBytes[nIdx++] = 128 + (nChr >>> 6 & 63);
                aBytes[nIdx++] = 128 + (nChr & 63);
            } else if (nChr < 0x200000) {
                /* four bytes */
                aBytes[nIdx++] = 240 + (nChr >>> 18);
                aBytes[nIdx++] = 128 + (nChr >>> 12 & 63);
                aBytes[nIdx++] = 128 + (nChr >>> 6 & 63);
                aBytes[nIdx++] = 128 + (nChr & 63);
            } else if (nChr < 0x4000000) {
                /* five bytes */
                aBytes[nIdx++] = 248 + (nChr >>> 24);
                aBytes[nIdx++] = 128 + (nChr >>> 18 & 63);
                aBytes[nIdx++] = 128 + (nChr >>> 12 & 63);
                aBytes[nIdx++] = 128 + (nChr >>> 6 & 63);
                aBytes[nIdx++] = 128 + (nChr & 63);
            } else /* if (nChr <= 0x7fffffff) */ {
                /* six bytes */
                aBytes[nIdx++] = 252 + (nChr >>> 30);
                aBytes[nIdx++] = 128 + (nChr >>> 24 & 63);
                aBytes[nIdx++] = 128 + (nChr >>> 18 & 63);
                aBytes[nIdx++] = 128 + (nChr >>> 12 & 63);
                aBytes[nIdx++] = 128 + (nChr >>> 6 & 63);
                aBytes[nIdx++] = 128 + (nChr & 63);
            }
        }

        return aBytes;

    }

    static encode(str) {
        var utf8input = B64.strToUTF8Arr(str);
        return B64.base64EncArr(utf8input);
    }

    static decode(b64) {
        var utf8output = B64.base64DecToArr(b64);
        return B64.UTF8ArrToStr(utf8output);
    }

}


class CeasarCipher {

    static alphanumericChars = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
        'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
        'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    ];

    static base64Chars = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
        'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
        'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    ];

    static encrypt(message, shift, base64) {
        var alphabet = CeasarCipher.alphanumericChars;
        if (base64 == 'true' || base64 == true) {
            alphabet = CeasarCipher.base64Chars;
            message = B64.encode(message);
        }
        console.log('encrypting message: ' + message);
        shift = parseInt(shift);
        console.log('alphabet.length = ' + alphabet.length);
        console.log('shift = ' + shift);
        var encrypted = '';
        message.split('').forEach(function(c) {
            var oldIndex = alphabet.indexOf(c);
            console.log(c + ' oldIndex = ' + oldIndex)
            if (oldIndex < 0) encrypted = encrypted + c;
            else {
                var newIndex = (oldIndex + shift) % alphabet.length;
                console.log('newIndex = ' + newIndex);
                encrypted = encrypted + alphabet[newIndex];
            }
        });
        console.log('encrypted: ' + encrypted);
        return encrypted;
    }

    static decrypt(message, shift, base64) {
        var decrypted = '';
        var alphabet = CeasarCipher.alphanumericChars;
        if (base64 == 'true' || base64 == true) {
            alphabet = CeasarCipher.base64Chars;
        }
        message.split('').forEach(function(c) {
            var oldIndex = alphabet.indexOf(c);
            if (oldIndex < 0) decrypted = decrypted + c;
            else {
                var newIndex = (alphabet.length + oldIndex - shift) % alphabet.length;
                decrypted = decrypted + alphabet[newIndex];
            }
        });
        if (base64 == 'true' || base64 == true) {
            decrypted = B64.decode(decrypted);
        }
        return decrypted;
    }

}

class RSA {

    static modPow(a, b, n) {
        a = a % n;
        var result = 1n;
        var x = a;

        while (b > 0) {
            var leastSignificantBit = b % 2n;
            b = b / 2n;

            if (leastSignificantBit == 1n) {
                result = result * x;
                result = result % n;
            }

            x = x * x;
            x = x % n;
        }
        return result;
    }

    static decryptBigInt(bigInt, modulus, privateKey) {
        console.log('decrypting big int: ' + bigInt);
        var decrypted = RSA.modPow(bigInt, privateKey, modulus);
        console.log('decrypted big int: ' + decrypted);
        return decrypted;
    }

    static decryptBytes(bytes, modulus, privateKey) {
        console.log('decrypting bytes: ' + bytes);
        var modulusBytes = BigInt(modulus).toString(16).length / 2;
        var decrypted = [];
        while (bytes.length > 0) {
            var head = bytes.slice(0, modulusBytes);
            bytes = bytes.slice(modulusBytes);
            var headBigInt = RSA.bytesToBigInt(head);
            var decryptedBigInt = RSA.decryptBigInt(headBigInt, modulus, privateKey);
            var decryptedBytes = RSA.bigIntToBytes(decryptedBigInt);
            decrypted = decrypted.concat(decryptedBytes);
        }
        console.log('decrypted bytes: ' + decrypted);
        return decrypted;
    }

    static decrypt(message, modulus, privateKey) {
        console.log('decrypting string: ' + message);
        if (typeof modulus == 'string') modulus = BigInt(modulus);
        if (typeof privateKey == 'string') privateKey = BigInt(privateKey);
        var bytes = B64.base64DecToArr(message.trim());
        var decryptedBytes = RSA.decryptBytes(bytes, modulus, privateKey);
        var decrypted = B64.UTF8ArrToStr(decryptedBytes);
        console.log('decrypted string: ' + decrypted);
        return decrypted;
    }

    static encryptBigInt(bigInt, modulus, publicKey) {
        console.log('encrypting big int: ' + bigInt);
        var encrypted = RSA.modPow(bigInt, publicKey, modulus);
        console.log('encrypted big int: ' + encrypted);
        return encrypted;
    }

    static encryptBytes(bytes, modulus, publicKey) {
        console.log('encrypting bytes: ' + bytes);
        var modulusBytes = BigInt(modulus).toString(16).length / 2;
        var result = [];
        while (bytes.length > 0) {
            var head = bytes.slice(0, modulusBytes - 1);
            bytes = bytes.slice(modulusBytes - 1);
            console.log('took ' + head.length + ' bytes')
            var encryptedHeadBigInt = RSA.encryptBigInt(RSA.bytesToBigInt(head), modulus, publicKey);
            var encryptedHead = RSA.bigIntToBytes(encryptedHeadBigInt);
            var zeros = [];
            for (var i = 0; i < modulusBytes - encryptedHead.length; i++) {
                zeros[i] = 0;
            }
            encryptedHead = zeros.concat(encryptedHead);
            result = result.concat(encryptedHead);
        }
        console.log('encrypted bytes: ' + result);
        return result;
    }

    static encrypt(message, modulus, publicKey) {
        console.log('encrypting string: ' + message);
        if (typeof modulus == 'string') modulus = BigInt(modulus);
        if (typeof publicKey == 'string') publicKey = BigInt(publicKey);
        var bytes = B64.strToUTF8Arr(message.trim());
        var encryptedBytes = RSA.encryptBytes(bytes, modulus, publicKey);
        var encrypted = B64.base64EncArr(encryptedBytes);
        console.log('encrypted string: ' + encrypted);
        return encrypted;
    }

    static bigIntToBytes(i) {
        var bytes = [];
        var hex = i.toString(16);
        var i = 0;
        while (hex.length > 0) {
            bytes[i] = parseInt('0x' + hex.slice(0, 2));
            hex = hex.slice(2)
            i++;
        }
        return bytes;
    }

    static bytesToBigInt(bytes) {
        var multiplier = 1n;
        var result = 0n;
        for (var i = bytes.length; i > 0; i--) {
            result = result + BigInt(bytes[i - 1]) * multiplier;
            multiplier = multiplier * 256n;
        }
        return result;
    }

    static sqrt(value) {
        if (value < 0n) {
            throw 'square root of negative numbers is not supported'
        }

        if (value < 2n) {
            return value;
        }

        function newtonIteration(n, x0) {
            const x1 = ((n / x0) + x0) >> 1n;
            if (x0 === x1 || x0 === (x1 - 1n)) {
                return x0;
            }
            return newtonIteration(n, x1);
        }

        return newtonIteration(value, 1n);
    }

    static leastFactor(n) {
        if (n === 0n) return 0n;
        else if (n % 1n || n * n < 2n) return 1n;
        else if (n % 2n === 0n) return 2n;
        else if (n % 3n === 0n) return 3n;
        else if (n % 5n === 0n) return 5n;

        var m = RSA.sqrt(n);
        for (var i = 7n; i <= m; i += 30n) {
            if (n % i === 0n) return i;
            else if (n % (i + 4n) === 0n) return i + 4n;
            else if (n % (i + 6n) === 0n) return i + 6n;
            else if (n % (i + 10n) === 0n) return i + 10n;
            else if (n % (i + 12n) === 0n) return i + 12n;
            else if (n % (i + 16n) === 0n) return i + 16n;
            else if (n % (i + 22n) === 0n) return i + 22n;
            else if (n % (i + 24n) === 0n) return i + 24n;
        }
        return n;
    }

    static isPrime(value) {
        return bigInt(value).isProbablePrime(5); // todo remove bigInt library
    }

    static generatePrime(bits) {
        if (typeof bits == 'number' && bits % 8 == 0) {
            var hex = '';
            for (var i = 0; i < bits / 8; i++) {
                hex = hex + Math.floor(Math.random() * 256).toString(16);
            }
            var prime = BigInt('0x' + hex);
            while (!RSA.isPrime(prime)) {
                prime = prime + 1n;
            }
            return prime;
        } else {
            // error?
        }
    }

    static generateKeys(strength) {
        var phi = 0n
        var modulus = 0n
        while (modulus.toString(16).length / 2 != strength / 8) {
            var p = RSA.generatePrime(strength / 2 + 8);
            var q = RSA.generatePrime(strength / 2 + 8);
            phi = (p - 1n) * (q - 1n)
            modulus = p * q
        }

        var publicKey = 65537n // this shocked me also
        var privateKey = BigInt(bigInt(publicKey).modInv(phi))
        return { privateKey, publicKey, modulus };
    }

}