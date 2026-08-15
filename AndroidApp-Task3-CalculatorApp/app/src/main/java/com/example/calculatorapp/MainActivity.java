package com.example.calculatorapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView displayTextView;

    private StringBuilder currentInput = new StringBuilder();
    private StringBuilder expression = new StringBuilder();

    private String operator = "";
    private double firstOperand = 0;

    private boolean isOperatorClicked = false;
    private boolean isErrorState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayTextView = findViewById(R.id.displayTextView);

        currentInput.append("0");
        updateDisplay();
    }

    public void onNumberClick(View view) {

        if (isErrorState) {
            clearAll();
        }

        Button button = (Button) view;
        String digit = button.getText().toString();

        if (isOperatorClicked) {
            currentInput.setLength(0);
            isOperatorClicked = false;
        }

        if (currentInput.toString().equals("0")) {
            currentInput.setLength(0);
        }

        currentInput.append(digit);
        expression.append(digit);

        updateDisplay();
    }

    public void onDecimalClick(View view) {

        if (isErrorState) {
            clearAll();
        }

        if (isOperatorClicked) {
            currentInput.setLength(0);
            currentInput.append("0");
            isOperatorClicked = false;
            expression.append("0");
        }

        if (!currentInput.toString().contains(".")) {
            currentInput.append(".");
            expression.append(".");
            updateDisplay();
        }
    }

    public void onOperatorClick(View view) {

        if (isErrorState) {
            return;
        }

        Button button = (Button) view;
        String newOperator = button.getText().toString();

        if (isOperatorClicked) {
            operator = newOperator;

            if (expression.length() > 0) {
                expression.setCharAt(
                        expression.length() - 2,
                        newOperator.charAt(0)
                );
            }

            updateDisplay();
            return;
        }

        try {
            firstOperand = Double.parseDouble(currentInput.toString());
        } catch (NumberFormatException e) {
            showError();
            return;
        }

        operator = newOperator;

        expression.append(" ");
        expression.append(newOperator);
        expression.append(" ");

        isOperatorClicked = true;

        updateDisplay();
    }

    public void onEqualsClick(View view) {

        if (isErrorState) {
            return;
        }

        if (operator.isEmpty()) {
            return;
        }

        if (isOperatorClicked) {
            return;
        }

        calculateResult();

        operator = "";
    }

    public void onClearClick(View view) {
        clearAll();
    }

    public void onBackspaceClick(View view) {

        if (isErrorState) {
            clearAll();
            return;
        }

        if (isOperatorClicked) {
            return;
        }

        if (currentInput.length() > 0) {
            currentInput.deleteCharAt(currentInput.length() - 1);

            if (expression.length() > 0) {
                expression.deleteCharAt(expression.length() - 1);
            }
        }

        if (currentInput.length() == 0) {
            currentInput.append("0");

            if (expression.length() == 0) {
                expression.append("0");
            }
        }

        updateDisplay();
    }

    private void calculateResult() {

        double secondOperand;

        try {
            secondOperand = Double.parseDouble(currentInput.toString());
        } catch (NumberFormatException e) {
            showError();
            return;
        }

        double result;

        switch (operator) {

            case "+":
                result = firstOperand + secondOperand;
                break;

            case "−":
                result = firstOperand - secondOperand;
                break;

            case "×":
                result = firstOperand * secondOperand;
                break;

            case "÷":
                if (secondOperand == 0) {
                    showError();
                    return;
                }

                result = firstOperand / secondOperand;
                break;

            case "%":
                if (secondOperand == 0) {
                    showError();
                    return;
                }

                result = firstOperand % secondOperand;
                break;

            default:
                return;
        }

        String resultString;

        if (result == (long) result) {
            resultString = String.valueOf((long) result);
        } else {
            resultString = String.valueOf(result);
        }

        currentInput.setLength(0);
        currentInput.append(resultString);

        expression.setLength(0);
        expression.append(resultString);

        isOperatorClicked = false;

        updateDisplay();
    }

    private void clearAll() {

        currentInput.setLength(0);
        currentInput.append("0");

        expression.setLength(0);

        operator = "";
        firstOperand = 0;

        isOperatorClicked = false;
        isErrorState = false;

        updateDisplay();
    }

    private void showError() {

        displayTextView.setText("Error");

        currentInput.setLength(0);
        expression.setLength(0);

        isErrorState = true;

        operator = "";
        firstOperand = 0;

        isOperatorClicked = false;
    }

    private void updateDisplay() {
        displayTextView.setText(expression.toString());
    }
}