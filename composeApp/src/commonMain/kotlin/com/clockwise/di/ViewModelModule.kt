package com.clockwise.di

import com.clockwise.features.availability.calendar.presentation.CalendarViewModel
import com.clockwise.features.business.presentation.BusinessViewModel
import com.clockwise.features.business.presentation.add_employee.SearchViewModel
import com.clockwise.features.profile.presentation.ProfileViewModel
import com.clockwise.features.shift.schedule.presentation.WeeklyScheduleViewModel
import com.clockwise.features.welcome.presentation.WelcomeViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModelModule = module {
    // Feature-specific viewModels
    factory { WelcomeViewModel(get()) }
    factory { WeeklyScheduleViewModel(get()) }
    factory { CalendarViewModel(get()) }
    factory { BusinessViewModel(get()) }
    factory { ProfileViewModel(get()) }
    factory { SearchViewModel(get(), get()) }
} 