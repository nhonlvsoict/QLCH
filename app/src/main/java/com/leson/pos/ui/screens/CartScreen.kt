package com.leson.pos.ui.screens

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.*
import android.print.pdf.PrintedPdfDocument
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.leson.pos.data.repo.Repo
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CartScreen() {
  val ctx = LocalContext.current
  val items by Repo.observeOrderItems("TODO").collectAsState(initial = emptyList()) // wire orderId as needed
  var copies by remember { mutableStateOf(3) }

  Scaffold(topBar = { TopAppBar(title = { Text("Cart") }) },
    bottomBar = {
      BottomAppBar {
        Row {
          Text("Copies:")
          Spacer(Modifier.width(8.dp))
          var s by remember { mutableStateOf("3") }
          OutlinedTextField(value = s, onValueChange = { v -> s = v; copies = v.filter { it.isDigit() }.toIntOrNull()?.coerceIn(1,10) ?: 3 },
            singleLine = true, modifier = Modifier.width(80.dp))
        }
        Spacer(Modifier.weight(1f))
        Button(onClick = {
          val pm = ctx.getSystemService(Context.PRINT_SERVICE) as PrintManager
          pm.print("LeSon-Receipt", printAdapterForOrder(ctx, "ORDER_ID_PLACEHOLDER", copies, finalBill = false), null)
        }) { Text("Print Receipts") }
        Spacer(Modifier.width(8.dp))
        Button(onClick = {
          val pm = ctx.getSystemService(Context.PRINT_SERVICE) as PrintManager
          pm.print("LeSon-FinalBill", printAdapterForOrder(ctx, "ORDER_ID_PLACEHOLDER", copies, finalBill = true), null)
        }) { Text("Print Final Bill") }
      }
    }
  ) { p ->
    LazyColumn(Modifier.padding(p)) {
      items(items) { it ->
        ListItem(headlineContent={ Text("${it.qty} × ${it.name}") }, supportingContent={ Text(it.note ?: "") }, trailingContent = { Text("£" + "%.2f".format((it.unitPriceCents*it.qty)/100.0)) })
        Divider()
      }
    }
  }
}

fun printAdapterForOrder(ctx: Context, orderId: String, copies: Int, finalBill: Boolean = false): PrintDocumentAdapter {
  return object: PrintDocumentAdapter() {
    private var pdf: PrintedPdfDocument? = null
    override fun onLayout(oldAttributes: PrintAttributes?, newAttributes: PrintAttributes, cancellationSignal: CancellationSignal?, callback: LayoutResultCallback, extras: Bundle?) {
      pdf = PrintedPdfDocument(ctx, newAttributes)
      val info = PrintDocumentInfo.Builder(if (finalBill) "final_bill.pdf" else "receipt.pdf")
        .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
        .setPageCount(copies)
        .build()
      callback.onLayoutFinished(info, true)
    }
    override fun onWrite(pages: Array<out PageRange>?, destination: ParcelFileDescriptor, cancellationSignal: CancellationSignal?, callback: WriteResultCallback) {
      val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.UK).format(Date())
      for (i in 0 until copies) {
        val page = pdf!!.startPage(i)
        val c = page.canvas
        val paint = Paint().apply { textSize = 14f }
        val small = Paint().apply { textSize = 12f }
        var y = 40f
        val title = if (finalBill) "FINAL BILL" else "Receipt"

        c.drawText("LeSon Restaurant", 40f, y, paint); y += 20f
        c.drawText("$title — Copy ${i+1} of $copies", 40f, y, paint); y += 16f
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