package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView tvExpression, tvResult;
    private String currentInput = "0";
    private double firstOperand = Double.NaN;
    private Operator currentOperator = Operator.NONE;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##########");
    private boolean isResultDisplayed = false;

    enum Operator {
        NONE(""), ADD("+"), SUBTRACT("-"), MULTIPLY("x"), DIVIDE("/");

        final String symbol;
        Operator(String symbol) {
            this.symbol = symbol;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
    }

    private void initViews() {
        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);

        tvExpression.setText("");
        tvResult.setText("0");

        int[] numericButtons = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btn00, R.id.btnDot
        };

        for (int id : numericButtons) {
            findViewById(id).setOnClickListener(this::onNumberClick);
        }

        findViewById(R.id.btnPlus).setOnClickListener(v -> setOperator(Operator.ADD));
        findViewById(R.id.btnMinus).setOnClickListener(v -> setOperator(Operator.SUBTRACT));
        findViewById(R.id.btnMul).setOnClickListener(v -> setOperator(Operator.MULTIPLY));
        findViewById(R.id.btnDiv).setOnClickListener(v -> setOperator(Operator.DIVIDE));
        findViewById(R.id.btnClear).setOnClickListener(v -> clear());
        findViewById(R.id.btnDel).setOnClickListener(v -> delete());
        findViewById(R.id.btnPercent).setOnClickListener(v -> percent());
        findViewById(R.id.btnEqual).setOnClickListener(v -> calculateResult());
    }

    private void onNumberClick(View v) {
        if (isResultDisplayed) {
            clear();
        }
        isResultDisplayed = false;

        String val = ((Button) v).getText().toString();

        if (val.equals(".")) {
            if (currentInput.equals("0") || currentInput.isEmpty()) {
                currentInput = "0.";
            } else if (!currentInput.contains(".")) {
                currentInput += ".";
            }
        } else {
            if (currentInput.equals("0")) {
                if (!val.equals("0") && !val.equals("00")) {
                    currentInput = val;
                }
            } else {
                currentInput += val;
            }
        }

        tvResult.setText(currentInput);
        updateExpression();
    }

    private void setOperator(Operator op) {
        if (!currentInput.equals("0")) {
            if (!Double.isNaN(firstOperand) && currentOperator != Operator.NONE) {
                calculateResult();
            } else {
                firstOperand = parse(currentInput);
            }
            currentInput = "0";
        } else if (Double.isNaN(firstOperand)) {
            firstOperand = 0;
        }

        isResultDisplayed = false;
        currentOperator = op;
        updateExpression();
    }

    private void updateExpression() {
        String expr = Double.isNaN(firstOperand) ? "" : decimalFormat.format(firstOperand) + " " + currentOperator.symbol;
        tvExpression.setText(expr + (currentInput.equals("0") ? "" : " " + currentInput));
    }

    private void calculateResult() {
        if (Double.isNaN(firstOperand) || currentOperator == Operator.NONE) return;

        double secondOperand = parse(currentInput);
        double result = 0;

        switch (currentOperator) {
            case ADD: result = firstOperand + secondOperand; break;
            case SUBTRACT: result = firstOperand - secondOperand; break;
            case MULTIPLY: result = firstOperand * secondOperand; break;
            case DIVIDE:
                if (secondOperand != 0) result = firstOperand / secondOperand;
                else {
                    tvResult.setText("Error");
                    return;
                }
                break;
        }

        tvResult.setText(decimalFormat.format(result));
        tvExpression.setText(decimalFormat.format(firstOperand) + " " + currentOperator.symbol + " " + decimalFormat.format(secondOperand) + " =");

        firstOperand = result;
        currentInput = "0";
        currentOperator = Operator.NONE;
        isResultDisplayed = true;
    }

    private void clear() {
        currentInput = "0";
        firstOperand = Double.NaN;
        currentOperator = Operator.NONE;
        isResultDisplayed = false;
        tvExpression.setText("");
        tvResult.setText("0");
    }

    private void delete() {
        if (isResultDisplayed) {
            clear();
            return;
        }
        if (currentInput.length() > 1) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        } else {
            currentInput = "0";
        }
        tvResult.setText(currentInput);
        updateExpression();
    }

    private void percent() {
        if (isResultDisplayed) {
            firstOperand /= 100;
            tvResult.setText(decimalFormat.format(firstOperand));
            tvExpression.setText(decimalFormat.format(firstOperand));
        } else {
            currentInput = decimalFormat.format(parse(currentInput) / 100);
            tvResult.setText(currentInput);
            updateExpression();
        }
    }

    private double parse(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0; }
    }
}
