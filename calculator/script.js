const resultText = document.querySelector('.result');
const calculText = document.querySelector('.calcul');
const btns = document.querySelectorAll('.inputs button');

let lastOperatorPos = null;

//init buttons value
(() => {
    for (let i = 0; i < btns.length; i++) {
        const btn = btns[i];
        btn.value = btn.textContent;
    }

    const btnBackspace = document.getElementById('backspace');
    btnBackspace.value = 'backspace';
})();

//event listener on each buttons
for (let i = 0; i < btns.length; i++) {
    const btn = btns.item(i);
    btn.addEventListener('click', (ev) => {
        buttonAction(btn.value);
    });
}

const buttonAction = (btnValue) => {
    if(btnValue >= 0 || btnValue <= 9 || btnValue === '.') {
        calculText.textContent += btnValue;
    }
    else {
        applyOperator(btnValue);
    }
};

const applyOperator = (btnValue) => {
    switch (btnValue) {
        case 'C':
            lastOperatorPos = null; 
            updateResult(0);
            break;
        case '%': parsePercent();
            break;
        case '/': updateCalculOperator('/');
            break;
        case 'x': updateCalculOperator('*');
            break;
        case '-': updateCalculOperator('-');
            break;
        case '+': updateCalculOperator('+');
            break;
        case '=': calulateResult();
            break;
        case 'backspace': removeLastInput();
            break;
        default:
            break;
    }
};

const parsePercent = () => {
    let txt = calculText.textContent;
    let percentNumber = null;

    //get number between last operator and end of string
    if(lastOperatorPos) {
        percentNumber = txt.slice(lastOperatorPos, txt.length);
    }
    else {
        percentNumber = txt.slice(0, txt.length);
    }

    //remove percentNumber to txt
    txt = txt.slice(0, (txt.length - percentNumber.length));
    //change to percent number
    txt = txt + ('' + parseFloat(percentNumber / 100).toFixed(2));
    calculText.textContent = txt;
};

const updateCalculOperator = (value) => {
    calculText.textContent += value;
    lastOperatorPos = calculText.textContent.length;
};

const removeLastInput = () => {
    const txt = calculText.textContent;
    const lastInput = txt.length -1;

    if(txt.length === lastOperatorPos)
        lastOperatorPos = null;

    calculText.textContent = txt.slice(0, lastInput);
};

const calulateResult = () => {
    if(calculText.textContent === '') {
        return;
    }
    else {
        try {
            const currentResult = eval?.(`"use strict"; (${calculText.textContent})`);
            lastOperatorPos = null;
            updateResult(currentResult);
            
        } catch (error) {
            console.log(calculText.textContent);
            console.error(error);
            lastOperatorPos = null;
            updateResult(NaN);
        }
    }
};

const updateResult = (newResult) => {
    resultText.textContent = newResult;
    calculText.textContent = null;
};

