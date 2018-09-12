package com.task.test.bdswiss

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import com.github.mikephil.charting.data.Entry
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.task.test.bdswiss.api.BDService
import com.task.test.bdswiss.api.MockService
import com.task.test.bdswiss.models.Rate
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    val bdService by lazy {
        BDService.create()
    }

    val mockService by lazy {
        MockService.create(this)
    }

    private var disposable: Disposable? = null

    val entries = arrayListOf<Entry>()
    var time = 0f

    private val rnd = Random()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getRates()
    }

    private fun getMockRates() {
        disposable =
                Observable.interval(0, 10, TimeUnit.SECONDS)
                        .flatMap { mockService.getRates() }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { result ->

                                    time += 10
                                    updateRates(result.rates)

                                },
                                { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }
                        )
    }

    private fun getRates() {
        disposable =
                Observable.interval(0, 10, TimeUnit.SECONDS)
                        .flatMap { bdService.getRates() }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { result ->

                                    time += 10
                                    updateRates(result.rates)

                                },
                                { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }
                        )

    }

    private fun updateRates(rates: List<Rate>) {

        prepareTableAndChart(rates)

        rates.forEachIndexed { index, rate ->

            val row = rateTable.getChildAt(index) as TableRow

            val symbol = row.getChildAt(0) as TextView
            val price = row.getChildAt(1) as TextView

            if (!price.text.isEmpty()) {
                val prevPrice = price.text.toString().toDouble()
                when (prevPrice < rate.price) {
                    true -> {
                        price.setBackgroundColor(Color.GREEN)
                    }
                    false -> {
                        price.setBackgroundColor(Color.RED)
                    }
                }
            }

            symbol.text = rate.symbol
            price.text = rate.price.toString().dropLast(10)

            chart.data.getDataSetByIndex(index).addEntry(Entry(time, rate.price.toFloat()))

        }

        chart.data.notifyDataChanged()
        chart.notifyDataSetChanged()
        chart.invalidate()

    }

    private fun prepareTableAndChart(rates: List<Rate>) {

        if (rateTable.childCount == rates.size) return

        var lines = arrayListOf<LineDataSet>()

        for (i in 0 until rates.size) {

            val color = Color.argb(255,
                    rnd.nextInt(256),
                    rnd.nextInt(256),
                    rnd.nextInt(256))

            val rate = rates[i]
            val row = TableRow(this)

            val symbol = TextView(this)
            val price = TextView(this)

            symbol.gravity = Gravity.CENTER
            price.gravity = Gravity.CENTER

            symbol.setTextColor(color)
            price.setBackgroundColor(Color.GRAY)

            row.addView(symbol)
            row.addView(price)
            rateTable.addView(row)

            val entries = arrayListOf<Entry>()
            entries.add(Entry(time, rate.price.toFloat()))

            val line = LineDataSet(entries, rate.symbol)

            line.color = color
            line.setCircleColor(color)
            line.setDrawCircles(true)
            line.setDrawValues(false)

            lines.add(line)

            chart.data = LineData(lines as List<ILineDataSet>?)

        }

    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}