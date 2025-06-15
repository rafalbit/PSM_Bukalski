package wat.psm_lab2_bukalski

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

import wat.psm_lab2_bukalski.db.PersonDB
import wat.psm_lab2_bukalski.db.PersonDao
import wat.psm_lab2_bukalski.db.PersonViewModel


val AppModule = module {

    // konfiguracja zależna od kontekstu Androida
    single { PersonDB.getPersonDB(androidContext()) }
    single<PersonDao> { get<PersonDB>().personDao() }
    viewModel { PersonViewModel(get()) }
    viewModel { UdpViewModel(get()) }

}
