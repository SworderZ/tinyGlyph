package space.megaworld.claudeusage

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import space.megaworld.claudeusage.data.NightSchedule

class NightScheduleTest {
    @Test fun `default schedule is quiet across midnight`() {
        val schedule = NightSchedule(enabled = true)
        assertFalse(schedule.isQuietAtMinutes(22 * 60 + 59))
        assertTrue(schedule.isQuietAtMinutes(23 * 60))
        assertTrue(schedule.isQuietAtMinutes(3 * 60))
        assertTrue(schedule.isQuietAtMinutes(6 * 60 + 59))
        assertFalse(schedule.isQuietAtMinutes(7 * 60))
    }

    @Test fun `disabled schedule never hides Glyph`() {
        val schedule = NightSchedule()
        assertFalse(schedule.isQuietAtMinutes(0))
        assertFalse(schedule.isQuietAtMinutes(23 * 60))
    }

    @Test fun `same-day range also works when user chooses daytime hours`() {
        val schedule = NightSchedule(enabled = true, startMinutes = 1 * 60, endMinutes = 3 * 60)
        assertFalse(schedule.isQuietAtMinutes(0))
        assertTrue(schedule.isQuietAtMinutes(2 * 60))
        assertFalse(schedule.isQuietAtMinutes(3 * 60))
    }
}
