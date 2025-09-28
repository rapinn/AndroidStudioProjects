package com.example.cs364_assignment1;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

/** BMI = weight(kg) / height(m)^2 */
public class MainActivity extends AppCompatActivity {

    private EditText etWeight, etHeight;
    private TextView tvBMI, tvStatusText;
    private MaterialCardView resultCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etWeight     = findViewById(R.id.etWeight);
        etHeight     = findViewById(R.id.etHeight);
        tvBMI        = findViewById(R.id.tvBMI);
        tvStatusText = findViewById(R.id.tvStatusText);
        resultCard   = findViewById(R.id.resultCard);
        Button btn   = findViewById(R.id.btnCalculate);

        resultCard.setVisibility(View.GONE);

        // จำกัดทศนิยม 2 หลัก
        InputFilter[] filters = new InputFilter[]{ new DecimalDigitsInputFilter(5, 2) };
        etWeight.setFilters(filters);
        etHeight.setFilters(filters);

        btn.setOnClickListener(v -> calculateAndShow());
    }

    private void calculateAndShow() {
        try {
            double w   = Double.parseDouble(etWeight.getText().toString().trim().replace(",", "."));
            double hCm = Double.parseDouble(etHeight.getText().toString().trim().replace(",", "."));
            if (w <= 0 || hCm <= 0) throw new NumberFormatException();

            double hM  = hCm / 100.0;
            double bmi = w / (hM * hM);

            DecimalFormat df = new DecimalFormat("#,##0.00");
            tvBMI.setText(df.format(bmi));

            // หมวด + ระดับความเสี่ยง + สีประกอบ
            String category, risk;
            int color;
            if (bmi < 18.5) {
                category = getString(R.string.category_underweight);
                risk = getString(R.string.risk_low);
                color = ContextCompat.getColor(this, R.color.risk_blue);
            } else if (bmi < 25.0) {
                category = getString(R.string.category_normal);
                risk = getString(R.string.risk_lowest);
                color = ContextCompat.getColor(this, R.color.risk_green);
            } else if (bmi < 30.0) {
                category = getString(R.string.category_overweight);
                risk = getString(R.string.risk_increased);
                color = ContextCompat.getColor(this, R.color.risk_orange);
            } else {
                category = getString(R.string.category_obese);
                risk = getString(R.string.risk_high);
                color = ContextCompat.getColor(this, R.color.risk_red);
            }
            tvStatusText.setText(getString(R.string.category_with_risk, category, risk));
            tvStatusText.setTextColor(color);

            resultCard.setVisibility(View.VISIBLE);

        } catch (NumberFormatException e) {
            Toast.makeText(this, getString(R.string.error_input), Toast.LENGTH_SHORT).show();
        }
    }

    /** กรองทศนิยม: x หลักก่อนจุด + y หลักหลังจุด */
    public static class DecimalDigitsInputFilter implements InputFilter {
        private final Pattern pattern;
        public DecimalDigitsInputFilter(int digitsBefore, int digitsAfter) {
            this.pattern = Pattern.compile("[0-9]{0," + digitsBefore + "}(\\.[0-9]{0," + digitsAfter + "})?");
        }
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            String newVal = new StringBuilder(dest)
                    .replace(dstart, dend, source.subSequence(start, end).toString())
                    .toString();
            return pattern.matcher(newVal).matches() ? null : "";
        }
    }
}
