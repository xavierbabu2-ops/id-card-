package com.example

import com.example.data.MemberCardEntity
import com.example.util.DistrictCodeHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

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
}
