const resultText = document.querySelector('.result');
const calculText = document.querySelector('.calcul');
const btns = document.querySelectorAll('.inputs button');

let currentValue = 0, result = 0;
let currentOperator = null;

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
        if (calculText.textContent.includes('.') && btnValue === '.') {
            return;
        }
        
        calculText.textContent += btnValue;
        try {
            console.log(calculText.textContent);
            currentValue = Number(result + eval(calculText.textContent));
            console.log(currentValue);
        } catch (error) {
            console.error(error);
            resetCalcul();
        }
    }
    else {
        applyOperator(btnValue);
    }
};

const applyOperator = (btnValue) => {
    switch (btnValue) {
        case 'C': resetCalcul();
            break;
        case '+/-': alert('Not available');
            break;
        case '%': alert('Not available');
            break;
        case '/': calculText.textContent += '/';
            break;
        case 'x': calculText.textContent += '*';
            break;
        case '-': calculText.textContent += '-';
            break;
        case '+': calculText.textContent += '+';
            break;
        case '=': calulateResult();
            break;
        case 'backspace': removeLastInput();
            break;
        default:
            break;
    }
};

const removeLastInput = () => {
    let txt = calculText.textContent;
    txt = txt.slice(0, txt.length -1);
    calculText.textContent = txt;
};

const calulateResult = () => {
    if(calculText.textContent === '') {
        updateResult(result);
    }
    else {
        updateResult(currentValue);
    }
};

const resetCalcul = () => {
    updateResult(0);
    currentValue = 0;
};

const updateResult = (newResult) => {
    result = newResult;
    resultText.textContent = result;
    calculText.textContent = '';
};


