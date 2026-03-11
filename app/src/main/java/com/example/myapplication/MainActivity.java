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
    private String currentInput = "";
    private double firstOperand = Double.NaN;
    private Operator currentOperator = Operator.NONE;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##########");

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

        // Gán listener cho các nút số
        int[] numericButtons = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btn00, R.id.btnDot
        };

        View.OnClickListener numberListener = v -> {
            Button b = (Button) v;
            String text = b.getText().toString();

            if (text.equals(".") && currentInput.contains(".")) return;
            
            currentInput += text;
            tvResult.setText(currentInput);
            updateExpression();
        };

        for (int id : numericButtons) {
            findViewById(id).setOnClickListener(numberListener);
        }

        // Gán listener cho các nút phép tính
        findViewById(R.id.btnPlus).setOnClickListener(v -> setOperator(Operator.ADD));
        findViewById(R.id.btnMinus).setOnClickListener(v -> setOperator(Operator.SUBTRACT));
        findViewById(R.id.btnMul).setOnClickListener(v -> setOperator(Operator.MULTIPLY));
        findViewById(R.id.btnDiv).setOnClickListener(v -> setOperator(Operator.DIVIDE));

        // Nút đặc biệt
        findViewById(R.id.btnClear).setOnClickListener(v -> clear());
        findViewById(R.id.btnDel).setOnClickListener(v -> delete());
        findViewById(R.id.btnPercent).setOnClickListener(v -> percent());
        findViewById(R.id.btnEqual).setOnClickListener(v -> calculateResult());
    }

    private void setOperator(Operator op) {
        if (!currentInput.isEmpty()) {
            if (!Double.isNaN(firstOperand) && currentOperator != Operator.NONE) {
                // Tính toán trung gian nếu đã có số thứ nhất và dấu
                calculateResult();
            }
            firstOperand = Double.parseDouble(currentInput);
            currentInput = "";
        } else if (Double.isNaN(firstOperand)) {
            firstOperand = 0;
        }

        currentOperator = op;
        updateExpression();
    }

    private void updateExpression() {
        String expr = "";
        if (!Double.isNaN(firstOperand)) {
            expr = decimalFormat.format(firstOperand) + " " + currentOperator.symbol;
        }
        if (!currentInput.isEmpty()) {
            expr += " " + currentInput;
        }
        tvExpression.setText(expr);
    }

    private void calculateResult() {
        if (Double.isNaN(firstOperand) || currentInput.isEmpty() || currentOperator == Operator.NONE) return;

        double secondOperand = Double.parseDouble(currentInput);
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
        tvExpression.setText(decimalFormat.format(firstOperand) + " " + currentOperator.symbol + " " + secondOperand + " =");
        
        // Chuẩn bị cho phép tính tiếp theo
        firstOperand = result;
        currentInput = "";
        currentOperator = Operator.NONE;
    }

    private void clear() {
        currentInput = "";
        firstOperand = Double.NaN;
        currentOperator = Operator.NONE;
        tvExpression.setText("");
        tvResult.setText("0");
    }

    private void delete() {
        if (!currentInput.isEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
            tvResult.setText(currentInput.isEmpty() ? "0" : currentInput);
            updateExpression();
        }
    }

    private void percent() {
        if (!currentInput.isEmpty()) {
            double value = Double.parseDouble(currentInput) / 100;
            currentInput = decimalFormat.format(value);
            tvResult.setText(currentInput);
            updateExpression();
        }
    }
}
