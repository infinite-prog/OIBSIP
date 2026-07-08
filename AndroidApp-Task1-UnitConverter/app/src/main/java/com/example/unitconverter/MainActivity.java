package com.example.unitconverter;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    Spinner spinnerCategory;
    Spinner spinnerSourceUnit;
    Spinner spinnerTargetUnit;
    EditText number;
    TextView output;

    String[] categories = {"Length", "Weight", "Temperature"};
    String[] lengthUnits = {"Meters", "Kilometers", "Feet", "Miles"};
    String[] weightUnits = {"Kilograms", "Grams", "Pounds", "Ounces"};
    String[] tempUnits = {"Celsius", "Fahrenheit", "Kelvin"};


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
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerSourceUnit = findViewById(R.id.spinnerSourceUnit);
        spinnerTargetUnit = findViewById(R.id.spinnerTargetUnit);
        number = findViewById(R.id.Number);
        output = findViewById(R.id.output);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUnitSpinners(categories[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void updateUnitSpinners(String category) {
        String[] units;

        switch (category) {
            case "Weight":
                units = weightUnits;
                break;
            case "Temperature":
                units = tempUnits;
                break;
            default:
                units = lengthUnits;
        }
                ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, units);
                unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerSourceUnit.setAdapter(unitAdapter);
                spinnerTargetUnit.setAdapter(unitAdapter);

    }

    public void onConvert(View view) {
        String input = number.getText().toString().trim();

        if (input.isEmpty()) {
            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();
            return;

        }

        double value;
        try {
            value = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
            return;
        }

        String category = spinnerCategory.getSelectedItem().toString();
        String fromUnit = spinnerSourceUnit.getSelectedItem().toString();
        String toUnit = spinnerTargetUnit.getSelectedItem().toString();

        double result;

        switch (category) {
            case ("Weight"):
                result = convertWeight(value, fromUnit, toUnit);
                break;
            case ("Temperature"):
                result = convertTemp(value, fromUnit, toUnit);
                break;
            default:
                result = convertLength(value, fromUnit, toUnit);
        }

        output.setText(String.format("Result: %.4f %s", result, toUnit));
    }

    public double convertLength(double value, String fromUnit, String toUnit) {
        double meters;
        switch (fromUnit) {
            case "Kilometers":
                meters = value * 1000;
                break;
            case "Feet":
                meters = value * 0.3048;
                break;
            case "Miles":
                meters = value * 1609.34;
                break;
            default:
                meters = value;
        }

        switch (toUnit) {
            case "Kilometers":
                return meters / 1000;
            case "Feet":
                return meters / 0.3048;
            case "Miles":
                return meters / 1609.34;
            default:
                return meters;
        }
    }

    public double convertWeight(double value, String fromUnit, String toUnit) {
        double grams;
        switch (fromUnit) {
            case ("Kilograms"):
                grams = value * 1000;
                break;
            case ("Pounds"):
                grams = value * 453.592;
                break;
            case ("Ounces"):
                grams = value * 28.3495;
                break;
            default:
                grams = value;
        }

        switch (toUnit) {
            case ("Kilograms"):
                return grams / 1000;
            case ("Pounds"):
                return grams / 453.592;
            case ("Ounces"):
                return grams / 28.3495;
            default:
                return grams;
        }
    }

    private double convertTemp(double value, String from, String to) {
        if (from.equals(to)) return value;

        double celsius;
        switch (from) {
            case "Fahrenheit":
                celsius = (value - 32) * 5 / 9;
                break;
            case "Kelvin":
                celsius = value - 273.15;
                break;
            default:
                celsius = value; // Celsius
        }

        switch (to) {
            case "Fahrenheit":
                return celsius * 9 / 5 + 32;
            case "Kelvin":
                return celsius + 273.15;
            default:
                return celsius;
        }
    }
}
