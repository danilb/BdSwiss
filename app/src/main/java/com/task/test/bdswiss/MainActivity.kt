package com.task.test.bdswiss

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
import com.task.test.bdswiss.api.BDService
import com.task.test.bdswiss.api.MockService
import com.task.test.bdswiss.models.Rate
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val bdService by lazy {
        BDService.create()
    }

    val mockService by lazy {
        MockService.create(this)
    }


    var disposable: Disposable? = null
    val entries = arrayListOf<Entry>()
    var time = 0f
    val dataSet = LineDataSet(entries, "Label")

    val lineData = LineData(dataSet)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getRates()
    }

    private fun getMockRates() {
        disposable =
                Observable.interval( 2, TimeUnit.SECONDS)
                        .flatMap { mockService.getRates() }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { result ->
                                    updateRateTable(result.rates)
                                },
                                { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }
                        )
    }

    private fun getRates() {
        disposable =
        Observable.interval( 0,5, TimeUnit.SECONDS)
                .flatMap { bdService.getRates() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->

                            /*time += 10
                            entries.add(Entry(time, result.rates.get(0).price.toFloat()))
                            chart.data = lineData
                            dataSet.notifyDataSetChanged()
                            lineData.notifyDataChanged()
                            chart.notifyDataSetChanged()
                            chart.invalidate()*/

                            updateRateTable(result.rates)
                        },
                        { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }
                )

    }

    private fun updateRateTable(rates: List<Rate>){

        prepareTable(rates)

        rates.forEachIndexed { index, rate ->

            val row = rateTable.getChildAt(index) as TableRow

            val symbol = row.getChildAt(0) as TextView
            val price = row.getChildAt(1) as TextView

            symbol.text = rate.symbol
            price.text = rate.price.toString()

        }
    }

    private fun prepareTable(rates: List<Rate>) {
        if (rateTable.childCount == 0) {
            for (i in 0 until rates.size) {
                val row = TableRow(this)

                val symbol = TextView(this)
                val price = TextView(this)

                symbol.gravity = Gravity.RIGHT

                row.addView(symbol)
                row.addView(price)
                rateTable.addView(row)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}
