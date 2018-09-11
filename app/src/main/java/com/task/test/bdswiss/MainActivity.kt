package com.task.test.bdswiss

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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

        ratesButton.setOnClickListener {
            getMockRates()
        }
    }

    private fun getMockRates() {
        disposable =
                Observable.interval( 2, TimeUnit.SECONDS)
                        .flatMap { mockService.getRates() }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { result ->
                                    ratesText.text = result.toString()
                                },
                                { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }
                        )
    }

    private fun getRates() {
        disposable =
        Observable.interval( 10, TimeUnit.SECONDS)
                .flatMap { bdService.getRates() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
                            time += 10
                            entries.add(Entry(time, result.rates.get(0).price.toFloat()))

                            chart.data = lineData
                            dataSet.notifyDataSetChanged()
                            lineData.notifyDataChanged()
                            chart.notifyDataSetChanged()
                            chart.invalidate()

                            ratesText.text = result.toString()
                        },
                        { error -> Toast.makeText(this, error.message, Toast.LENGTH_LONG).show() }
                )

    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}
