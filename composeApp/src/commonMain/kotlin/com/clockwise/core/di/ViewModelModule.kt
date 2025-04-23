package com.clockwise.core.di

import com.clockwise.features.availability.presentation.calendar.CalendarViewModel
import com.clockwise.features.business.presentation.BusinessViewModel
import com.clockwise.features.business.presentation.add_employee.SearchViewModel
import com.clockwise.features.profile.presentation.ProfileViewModel
import com.clockwise.features.shift.presentation.week_schedule.WeeklyScheduleViewModel
import com.clockwise.features.shift.presentation.welcome_shifts.WelcomeViewModel
import org.koin.dsl.module

val viewModelModule = module {
    // Feature-specific viewModels
    factory { WelcomeViewModel(get()) }
    factory { WeeklyScheduleViewModel(get()) }
    factory { CalendarViewModel(get()) }
    factory { BusinessViewModel(get()) }
    factory { SearchViewModel(get(), get()) }
    factory { ProfileViewModel(get()) }
} 