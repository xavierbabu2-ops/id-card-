package com.example

import com.example.data.MemberCardEntity
import com.example.util.DistrictCodeHelper
import com.example.util.PassportPhotoCropper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleUnitTest {

    @Test
    fun testAll38DistrictsCount() {
        assertEquals(38, DistrictCodeHelper.ALL_38_DISTRICTS.size)
        assertEquals(38, DistrictCodeHelper.ALL_38_DISTRICT_DETAILS.size)
    }

    @Test
    fun testAll38DistrictsMemberIdFormatStartingFrom0001() {
        for (district in DistrictCodeHelper.ALL_38_DISTRICTS) {
            val memberId = DistrictCodeHelper.generateDistrictMemberId(district, "MEMBER", 1)
            assertTrue(
                "District $district Member ID $memberId should end with 0001",
                memberId.endsWith("-0001")
            )
            assertTrue(
                "District $district Member ID $memberId should start with TN-",
                memberId.startsWith("TN-")
            )
        }
    }

    @Test
    fun testDistrictCodeMapping() {
        assertEquals("MDU", DistrictCodeHelper.getDistrictCode("மதுரை"))
        assertEquals("CHN", DistrictCodeHelper.getDistrictCode("சென்னை"))
        assertEquals("CBE", DistrictCodeHelper.getDistrictCode("கோயம்புத்தூர்"))
        assertEquals("TRY", DistrictCodeHelper.getDistrictCode("திருச்சிராப்பள்ளி"))
        assertEquals("SLM", DistrictCodeHelper.getDistrictCode("சேலம்"))
        assertEquals("TKS", DistrictCodeHelper.getDistrictCode("தென்காசி"))
        assertEquals("NIL", DistrictCodeHelper.getDistrictCode("நீலகிரி"))
    }

    @Test
    fun testSequentialIncrementPerDistrict() {
        val maduraiCards = listOf(
            MemberCardEntity(
                id = 1,
                memberId = "TN-MDU-0001",
                name = "Karthik",
                district = "மதுரை",
                jobTitle = "Painter",
                fatherName = "Muthu",
                age = "30",
                bloodGroup = "O+",
                address = "Madurai",
                phone = "9876543210"
            )
        )
        val nextId = DistrictCodeHelper.getNextMemberIdForDistrict("மதுரை", "MEMBER", maduraiCards)
        assertEquals("TN-MDU-0002", nextId)

        // For a new district with no cards, it must start with 0001
        val chennaiId = DistrictCodeHelper.getNextMemberIdForDistrict("சென்னை", "MEMBER", maduraiCards)
        assertEquals("TN-CHN-0001", chennaiId)
    }

    @Test
    fun testPincodeAndTownDetection() {
        assertEquals("சென்னை", DistrictCodeHelper.detectDistrictFromText("12, Anna Nagar, Chennai 600040"))
        assertEquals("மதுரை", DistrictCodeHelper.detectDistrictFromText("5 Melur Road, 625001"))
        assertEquals("கோயம்புத்தூர்", DistrictCodeHelper.detectDistrictFromText("Pollachi, 641001"))
        assertEquals("கன்னியாகுமரி", DistrictCodeHelper.detectDistrictFromText("Nagercoil, 629001"))
        assertEquals("நீலகிரி", DistrictCodeHelper.detectDistrictFromText("Coonoor, 643001"))
    }

    @Test
    fun testPhotoCropPresets() {
        val testBitmap = android.graphics.Bitmap.createBitmap(1000, 1400, android.graphics.Bitmap.Config.ARGB_8888)
        
        val formCrop = PassportPhotoCropper.cropPresetRegion(testBitmap, PassportPhotoCropper.CropPreset.FORM_TOP_RIGHT, "3:4")
        assertTrue(formCrop.width > 0 && formCrop.height > 0)
        assertEquals(3f / 4f, formCrop.width.toFloat() / formCrop.height.toFloat(), 0.05f)

        val aadhaarCrop = PassportPhotoCropper.cropPresetRegion(testBitmap, PassportPhotoCropper.CropPreset.AADHAAR_LEFT, "3:4")
        assertTrue(aadhaarCrop.width > 0 && aadhaarCrop.height > 0)
        assertEquals(3f / 4f, aadhaarCrop.width.toFloat() / aadhaarCrop.height.toFloat(), 0.05f)

        testBitmap.recycle()
        formCrop.recycle()
        aadhaarCrop.recycle()
    }
}
