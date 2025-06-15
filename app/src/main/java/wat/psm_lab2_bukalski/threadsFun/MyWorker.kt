package wat.psm_lab2_bukalski.threadsFun

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay

class MyWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // Symulacja zadania: 50 kroków, każdy trwający 200 ms
        // W każdej iteracji wysyłamy wartość postępu
        for (i in 1..50) {
            delay(200L)
            // Ustawienie postępu – wartość zwiększana o 10% za każdą iterację
            setProgress(workDataOf("progress" to (i * 2)))
        }
        return Result.success()
    }
}