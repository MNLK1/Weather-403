package com.example.weather.view;

import static java.time.temporal.ChronoUnit.DAYS;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.weather.models.Temperature;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class ChartView extends View {
    private DateTimeFormatter tsFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'00:00:00");

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Integer padding = 100;

    Paint paintClear = new Paint();
    Paint linear = new Paint();
    Paint paintTemperature = new Paint();


    List<Temperature> temperatures = Collections.emptyList();

    LocalDate dateFrom = LocalDate.now();
    LocalDate dateTo = LocalDate.now().minusDays(10);


    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);

        paintClear.setColor(Color.WHITE);
        paintClear.setAntiAlias(true);

        paintTemperature.setColor(Color.BLUE);
        paintTemperature.setAntiAlias(true);

        linear.setColor(Color.BLACK);
        linear.setAntiAlias(true);
    }


    public void update(List<Temperature> temperatures, LocalDate from, LocalDate to) {
        this.temperatures = temperatures;
        this.dateFrom = from;
        this.dateTo = to;
        invalidate();
    }


    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getWidth(), getHeight(), paintClear);

        Float width = (float) canvas.getWidth();
        Float height = (float) canvas.getHeight();
        Long days = DAYS.between(dateFrom, dateTo);

        if (days < 1) return;

        canvas.drawLine(padding, height - padding, width - padding, height - padding, linear);
        canvas.drawLine(padding, padding, padding, height - padding, linear);

        Long missDate = 1 + (days / 60);

        float stepW = (width - padding*2.f)/((float) days);

        for (int day = 0; day < days; day+=missDate) {
            String date = dateFrom.plusDays(day).format(dateFormatter);

            canvas.drawLine(day * stepW + padding, height-padding, day * stepW+ padding, height + 20 - padding, linear);
            canvas.rotate(-90);
            canvas.drawText(date, -(width + padding/2), day * stepW + padding + 5, linear);
            canvas.rotate(90);
        }

        float stepH = (height - padding*2.f)/(100f);

        for (int y = 0; y < 100; y++) {
            canvas.drawLine(padding - 20, height - (y * stepH) - padding, padding, height - (y * stepH) - padding, linear);
            canvas.drawText("" + (y-50), padding - 40, height - (y * stepH) - padding + 5, linear);
        }

        for (int i = 1; i < temperatures.size(); i++) {
            LocalDate d = LocalDate.parse(temperatures.get(i-1).ts, tsFormatter);
            Long day = DAYS.between(dateFrom, d);

            float p1x = day * stepW + padding;
            float p1y = height - ((temperatures.get(i-1).temperature + 50) * stepH) - padding + 5;
            float p2x = (day+1) * stepW + padding;
            float p2y = height - ((temperatures.get(i).temperature + 50) * stepH) - padding + 5;

            canvas.drawLine(p1x, p1y, p2x, p2y, paintTemperature);
        }

    }
}
