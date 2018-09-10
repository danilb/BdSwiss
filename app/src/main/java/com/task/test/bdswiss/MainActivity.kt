package com.task.test.bdswiss

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.github.mikephil.charting.data.Entry
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit
import android.R.attr.entries
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData





class MainActivity : AppCompatActivity() {

    val bdService by lazy {
        BDService.create()
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
            getRates()
        }
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
                        { error -> Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show() }
                )

    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
    }
}
