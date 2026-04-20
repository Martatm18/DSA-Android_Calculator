package com.example.androidcalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // 1. Declaramos las variables de nuestras vistas
    private TextView tvDisplay;
    private Switch switchRadDeg;

    // 2. Variables para manejar la lógica
    private double operand1 = Double.NaN;
    private String currentOperation = "";
    private boolean isNewInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 3. Enlazamos el código con las vistas del XML
        tvDisplay = findViewById(R.id.tvDisplay);
        switchRadDeg = findViewById(R.id.switchRadDeg);

        // 4. Asignamos la acción a los botones numéricos
        int[] numberButtons = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDot
        };
        for (int id : numberButtons) {
            findViewById(id).setOnClickListener(this::onNumberClick);
        }

        // 5. Asignamos la acción a los botones de operaciones básicas
        int[] operatorButtons = {R.id.btnAdd, R.id.btnSub, R.id.btnMul, R.id.btnDiv};
        for (int id : operatorButtons) {
            findViewById(id).setOnClickListener(this::onOperatorClick);
        }

        // 6. Asignamos la acción a los botones especiales
        findViewById(R.id.btnEq).setOnClickListener(v -> onEqualsClick());
        findViewById(R.id.btnClear).setOnClickListener(v -> onClearClick());
        findViewById(R.id.btnSin).setOnClickListener(v -> onTrigClick("Sin"));
        findViewById(R.id.btnCos).setOnClickListener(v -> onTrigClick("Cos"));
        findViewById(R.id.btnTan).setOnClickListener(v -> onTrigClick("Tan"));
    }

    // Método que se ejecuta al pulsar un número
    private void onNumberClick(View view) {
        Button button = (Button) view;
        if (isNewInput) {
            tvDisplay.setText("");
            isNewInput = false;
        }
        tvDisplay.append(button.getText());
    }

    // Método que se ejecuta al pulsar +, -, *, /
    private void onOperatorClick(View view) {
        Button button = (Button) view;
        String op = button.getText().toString();
        String displayText = tvDisplay.getText().toString();

        if (!displayText.isEmpty() && !displayText.equals("0")) {
            try {
                double currentValue = Double.parseDouble(displayText);

                // Evalúa de izquierda a derecha sin precedencia (ej: 1+2*5 = 15)
                if (!Double.isNaN(operand1) && !currentOperation.isEmpty() && !isNewInput) {
                    operand1 = calculate(operand1, currentValue, currentOperation);
                    tvDisplay.setText(formatResult(operand1));
                } else {
                    operand1 = currentValue;
                }
            } catch (NumberFormatException e) {
                return;
            }
        }
        currentOperation = op;
        isNewInput = true;
    }

    // Método que se ejecuta al pulsar "="
    private void onEqualsClick() {
        String displayText = tvDisplay.getText().toString();
        if (!displayText.isEmpty() && !Double.isNaN(operand1) && !currentOperation.isEmpty() && !isNewInput) {
            try {
                double currentValue = Double.parseDouble(displayText);
                double result = calculate(operand1, currentValue, currentOperation);
                tvDisplay.setText(formatResult(result));

                operand1 = result;
                currentOperation = "";
                isNewInput = true;
            } catch (NumberFormatException e) {
                return;
            }
        }
    }

    // Calculadora interna pura
    private double calculate(double val1, double val2, String op) {
        switch (op) {
            case "+": return val1 + val2;
            case "-": return val1 - val2;
            case "*": return val1 * val2;
            case "/": return (val2 != 0.0) ? val1 / val2 : Double.NaN;
            default: return val1;
        }
    }

    // Método que se ejecuta al pulsar Sin, Cos o Tan
    private void onTrigClick(String function) {
        String displayText = tvDisplay.getText().toString();
        if (displayText.isEmpty()) return;

        try {
            double value = Double.parseDouble(displayText);

            // Si el switch no está marcado, asumimos Grados y convertimos a Radianes para la función matemática
            if (!switchRadDeg.isChecked()) {
                value = Math.toRadians(value);
            }

            double result = value;
            switch (function) {
                case "Sin": result = Math.sin(value); break;
                case "Cos": result = Math.cos(value); break;
                case "Tan": result = Math.tan(value); break;
            }

            tvDisplay.setText(formatResult(result));
            operand1 = result;
            currentOperation = "";
            isNewInput = true;
        } catch (NumberFormatException e) {
            return;
        }
    }

    // Método que se ejecuta al pulsar la "C"
    private void onClearClick() {
        tvDisplay.setText("0");
        operand1 = Double.NaN;
        currentOperation = "";
        isNewInput = true;
    }

    //Método visual para quitar el ".0" de los números enteros
    private String formatResult(double result) {
        if (result == (long) result) {
            return String.valueOf((long) result);
        } else {
            return String.valueOf(result);
        }
    }
}