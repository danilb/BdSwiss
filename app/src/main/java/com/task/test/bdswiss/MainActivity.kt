package com.task.test.bdswiss

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    val bdService by lazy {
        BDService.create()
    }

    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ratesButton.setOnClickListener {
            getRates()
        }
    }

    private fun getRates() {
        disposable =
                

        Observable.interval( 2, TimeUnit.SECONDS)
                .flatMap { bdService.getRates() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->
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
