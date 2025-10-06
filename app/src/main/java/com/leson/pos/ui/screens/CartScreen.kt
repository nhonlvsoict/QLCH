package com.leson.pos.ui.screens

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.print.pdf.PrintedPdfDocument
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CartScreen() {
  val copies = remember { mutableStateOf(3) }
  val copiesText = remember { mutableStateOf("3") }

  androidx.compose.material3.Scaffold(
    topBar = { TopAppBar(title = { Text("Cart") }) },
    bottomBar = {
      BottomAppBar {
        Row {
          Text("Copies:")
          Spacer(Modifier.width(8.dp))
          OutlinedTextField(
            value = copiesText.value,
            onValueChange = { v ->
              copiesText.value = v
              copies.value = v.filter { it.isDigit() }.toIntOrNull()?.coerceIn(1, 10) ?: 3
            },
            singleLine = true,
            modifier = Modifier.width(80.dp)
          )
        }
        Spacer(Modifier.weight(1f))
        TextButton(onClick = {
          // You would look up the real orderId here
          // val id = currentOrderId
          // printAdapterForOrder(context, id, copies.value, finalBill = false)
        }) { Text("Print Receipts") }
        Spacer(Modifier.width(8.dp))
        TextButton(onClick = {
          // pm.print("LeSon-FinalBill", printAdapterForOrder(ctx, id, copies.value, finalBill = true), null)
        }) { Text("Print Final Bill") }
      }
    }
  ) { /* content omitted in MVP stub */ }
}

fun printAdapterForOrder(ctx: Context, orderId: String, copies: Int, finalBill: Boolean = false): PrintDocumentAdapter {
  return object : PrintDocumentAdapter() {
    private var pdf: PrintedPdfDocument? = null
    override fun onLayout(
      oldAttributes: PrintAttributes?,
      newAttributes: PrintAttributes,
      cancellationSignal: CancellationSignal?,
      callback: LayoutResultCallback,
      extras: Bundle?
    ) {
      pdf = PrintedPdfDocument(ctx, newAttributes)
      val info = PrintDocumentInfo.Builder(if (finalBill) "final_bill.pdf" else "receipt.pdf")
        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
        .setPageCount(copies)
        .build()
      callback.onLayoutFinished(info, true)
    }

    override fun onWrite(
      pages: Array<out PageRange>?,
      destination: ParcelFileDescriptor,
      cancellationSignal: CancellationSignal?,
      callback: WriteResultCallback
    ) {
      val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).format(Date())
      for (i in 0 until copies) {
        val page = pdf!!.startPage(i)
        val c = page.canvas
        val paint = Paint().apply { textSize = 14f }
        val small = Paint().apply { textSize = 12f }
        var y = 40f
        val title = if (finalBill) "FINAL BILL" else "Receipt"
        c.drawText("LeSon Restaurant", 40f, y, paint); y += 20f
        c.drawText("$title — Copy ${i + 1} of $copies", 40f, y, paint); y += 16f
        c.drawText("Order ID: $orderId", 40f, y, small); y += 16f
        c.drawText("Date: $now", 40f, y, small); y += 24f
        c.drawText("Items:", 40f, y, paint); y += 20f
        c.drawText("… identical content for each copy …", 40f, y, small)
        pdf!!.finishPage(page)
      }
      val out = ParcelFileDescriptor.AutoCloseOutputStream(destination)
      pdf!!.writeTo(out)
      out.close()
      callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
      pdf!!.close(); pdf = null
    }
  }
}
