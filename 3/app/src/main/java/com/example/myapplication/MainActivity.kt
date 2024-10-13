package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory
import kotlinx.coroutines.withContext

data class Currency(
    val charCode: String,  // Код валюты (например, "USD")
    val name: String,      // Название валюты (например, "Доллар США")
    val nominal: Int,      // Номинал (например, 1)
    val value: Double      // Текущий курс (например, 74.85)
)

class MainActivity : AppCompatActivity() {

    private lateinit var currency_from: Spinner
    private lateinit var currency_to: Spinner
    private lateinit var convert: Button
    private lateinit var converted: TextView
    private lateinit var money_value: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currency_from = findViewById(R.id.spinnerFromCurrency)
        currency_to = findViewById(R.id.spinnerToCurrency)
        convert = findViewById(R.id.btnConvert)
        converted = findViewById(R.id.tvResult)
        money_value = findViewById(R.id.etAmount)

        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            val url = URL("https://www.cbr.ru/scripts/XML_daily.asp")
            val convert_data = url.readText(Charsets.ISO_8859_1)
            val currency_list = parseXML(convert_data)

            val currencyNames = currency_list.map { it.charCode }

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, currencyNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                currency_from.adapter = adapter
                currency_to.adapter = adapter

                convert.setOnClickListener {
                    val amount = money_value.text.toString().toDoubleOrNull()
                    if (amount != null) {
                        val fromCurrency = currency_from.selectedItem.toString()
                        val toCurrency = currency_to.selectedItem.toString()

                        val fromCurrencyObj = currency_list.find { it.charCode == fromCurrency }
                        val toCurrencyObj = currency_list.find { it.charCode == toCurrency }

                        if (fromCurrencyObj != null && toCurrencyObj != null) {
                            val result = convertCurrency(amount, fromCurrencyObj, toCurrencyObj)
                            converted.text = String.format("%.2f", result)
                        } else {
                            converted.text = "Ошибка: валюта не найдена"
                        }
                    } else {
                        converted.text = "Ошибка: введите корректную сумму"
                    }
                }
            }
        }
    }

    fun parseXML(xml: String): List<Currency> {
        val currencies = mutableListOf<Currency>()

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val inputStream = xml.byteInputStream()
        val doc = builder.parse(inputStream)

        val nodeList: NodeList = doc.getElementsByTagName("Valute")

        for (i in 0 until nodeList.length) {
            val node = nodeList.item(i) as Element

            val charCode = node.getElementsByTagName("CharCode").item(0).textContent
            val name = node.getElementsByTagName("Name").item(0).textContent
            val nominal = node.getElementsByTagName("Nominal").item(0).textContent.toInt()
            val value = node.getElementsByTagName("Value").item(0).textContent.replace(",", ".").toDouble()

            // Создаем объект Currency и добавляем в список
            currencies.add(Currency(charCode, name, nominal, value))
        }

        return currencies
    }

    private fun convertCurrency(amount: Double, fromCurrency: Currency, toCurrency: Currency): Double {
        val fromRate = fromCurrency.value / fromCurrency.nominal
        val toRate = toCurrency.value / toCurrency.nominal
        return amount * (fromRate / toRate)
    }
}