package space.megaworld.claudeusage

import com.nothing.ketchum.Glyph
import com.nothing.ketchum.GlyphFrame
import org.junit.Assert.*
import org.junit.Test
import space.megaworld.claudeusage.data.AmbientSettings
import space.megaworld.claudeusage.data.GlyphLight
import space.megaworld.claudeusage.data.GlyphRenderMode
import space.megaworld.claudeusage.glyph.GlyphController

class GlyphBrightnessTest {
    private fun frame(request: GlyphController.Request, device: String = Glyph.DEVICE_23111) =
        GlyphController.compositeFrame(request, GlyphFrame.Builder(device)).channel

    @Test fun `dimming changes actual C channel intensity while keeping A and B intact on both phones`() {
        val settings = AmbientSettings(stripBrightnessPercent = 25)
        val request = GlyphController.Request(cPercent = 50, cLight = settings.stripLight,
            aLight = GlyphLight.FAINT, bLight = 1800)
        for (device in listOf(Glyph.DEVICE_23111, Glyph.DEVICE_23113)) {
            val channels = frame(request, device)
            assertEquals(1000, channels[Glyph.Code_23111.C_1])
            assertEquals(1000, channels[Glyph.Code_23111.C_12])
            assertEquals(0, channels[Glyph.Code_23111.C_13])
            assertEquals(0, channels[Glyph.Code_23111.C_24])
            assertEquals(GlyphLight.FAINT, channels[Glyph.Code_23111.A])
            assertEquals(1800, channels[Glyph.Code_23111.B])
        }
    }

    @Test fun `zero C brightness leaves the short channels lit`() {
        val request = GlyphController.Request(cPercent = 100, cLight = 0, aLight = 600, bLight = 1800)
        val channels = frame(request)
        assertEquals(0, channels[Glyph.Code_23111.C_1])
        assertEquals(0, channels[Glyph.Code_23111.C_24])
        assertEquals(600, channels[Glyph.Code_23111.A])
        assertEquals(1800, channels[Glyph.Code_23111.B])
        assertFalse(request.isBlank)
        assertTrue(request.copy(aLight = 0, bLight = 0).isBlank)
    }

    @Test fun `reversed dimmed progress keeps the selected direction and lit segment count`() {
        val channels = frame(GlyphController.Request(cPercent = 25, cLight = 400,
            mode = GlyphRenderMode.PROGRESS_REVERSED))
        assertEquals(400, channels[Glyph.Code_23111.C_24])
        assertEquals(400, channels[Glyph.Code_23111.C_19])
        assertEquals(0, channels[Glyph.Code_23111.C_18])
        assertEquals(0, channels[Glyph.Code_23111.C_1])
        assertEquals(6, channels.count { it == 400 })
    }

    @Test fun `dimmed strips bypass the SDK progress method that ignores brightness`() {
        assertFalse(GlyphController.Request(cPercent = 50).needsCustomFrame)
        assertTrue(GlyphController.Request(cPercent = 50, cLight = 1000).needsCustomFrame)
        assertTrue(GlyphController.Request(cPercent = 50, aLight = 600).needsCustomFrame)
    }
}
