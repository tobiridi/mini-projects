const uppercase = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];
const lowercase = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'];
const numbers = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9'];
const symbols = ['!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '-', '=', '[', ']', '{', '}', '|', '\\', ';', ':', "'", '"', ',', '.', '<', '>', '/', '?', '~', '`'];

const passStrength = document.getElementById('passStrength');
const passRange = document.getElementById('passRange');
const passGenerate = document.getElementById('passGenerate');
const checkUppercase = document.getElementById('uppercase');
const checkLowercase = document.getElementById('lowercase');
const checkNumbers = document.getElementById('numbers');
const checkSymbols = document.getElementById('symbols');
const generateBtn = document.getElementById('generateBtn');

generateBtn.addEventListener('click', (ev) => {
    generateNewPassword(passRange.value);
});

passRange.addEventListener('input', (ev) => {
    passStrength.textContent = passRange.value;
    generateNewPassword(passRange.value);
});

checkUppercase.addEventListener('change', (ev) => {
    isOneChecked() ? generateNewPassword(passRange.value) : checkUppercase.checked = true;
});

checkLowercase.addEventListener('change', (ev) => {
    isOneChecked() ? generateNewPassword(passRange.value) : checkLowercase.checked = true;
});

checkNumbers.addEventListener('change', (ev) => {
    isOneChecked() ? generateNewPassword(passRange.value) : checkNumbers.checked = true;
});

checkSymbols.addEventListener('change', (ev) => {
    isOneChecked() ? generateNewPassword(passRange.value) : checkSymbols.checked = true;
});

const generateNewPassword = (length) => {
    let newPassword = '';
    while(newPassword.length != length) {
        const array = Math.floor(Math.random() * 4);
        switch (array) {
            case 0: if(checkUppercase.checked) newPassword += uppercase[Math.floor(Math.random() * uppercase.length)];
                break;
            case 1: if(checkLowercase.checked) newPassword += lowercase[Math.floor(Math.random() * lowercase.length)];
                break;
            case 2: if(checkNumbers.checked) newPassword += numbers[Math.floor(Math.random() * numbers.length)];
                break;
            case 3: if(checkSymbols.checked) newPassword += symbols[Math.floor(Math.random() * uppercase.length)];
                break;
        }
    }
    passGenerate.value = newPassword;
};

const isOneChecked = () => {
    return (checkUppercase.checked || checkLowercase.checked
            || checkNumbers.checked || checkSymbols.checked);
};

generateNewPassword(passRange.value);