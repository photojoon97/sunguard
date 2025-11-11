package com.joon.sunguard_api.domain.busstop.service;

import com.joon.sunguard_api.domain.busstop.dto.response.BusStopResponse;
import com.joon.sunguard_api.domain.busstop.entity.BusStop;
import com.joon.sunguard_api.domain.busstop.repository.BusStopRepository;
import com.joon.sunguard_api.global.exception.CustomException;

import com.joon.sunguard_api.global.exception.ErrorCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("버스 정류장 테스트")
class BusstopServiceTest {

    @InjectMocks
    BusstopService busStopService;

    @Mock
    BusStopRepository busStopRepository;

    private List<BusStop> mockBusStops;

    private BusStop createBusStop(String stopId, String stopNo, String stopName, double gpsX, double gpsY) {
        return new BusStop(stopId, stopNo, stopName, gpsX, gpsY);
    }

    private List<BusStop> createMockBusStops() {
        return Arrays.asList(
                createBusStop("512450000", "05372", "e편한세상서면더센트럴", 129.050909264696, 35.151431778191),
                createBusStop("164630304", null, "롯데호텔백화점.서면역", 129.055430485308, 35.157857653006),
                createBusStop("164630302", "05190", "서면역.롯데호텔백화점", 129.055780198774, 35.157856952035),
                createBusStop("511400000", "05719", "서면역.롯데호텔백화점", 129.056177414543, 35.157481158902),
                createBusStop("164710101", "05319", "서면역.롯데호텔백화점", 129.056630088315, 35.157373152016),
                createBusStop("511370000", "05718", "서면역.롯데호텔백화점", 129.056694811946, 35.157765251146),
                createBusStop("164640301", "05236", "서면시장", 129.057669581521, 35.156109069511),
                createBusStop("164650201", "05234", "서면복개로", 129.057690834089, 35.153899533879),
                createBusStop("164650301", "05237", "롯데호텔백화점.서면역", 129.057902836401, 35.157077779665),
                createBusStop("164670502", "05241", "서면1번가", 129.057914834501, 35.154081952695),
                createBusStop("505850000", "05711", "서면역.서면지하상가", 129.059019125177, 35.155945959935),
                createBusStop("505880000", "05713", "서면한전", 129.059038073747, 35.153215114712),
                createBusStop("505860000", "05710", "서면역.서면지하상가", 129.059251862166, 35.155167003028),
                createBusStop("505870000", "05712", "서면한전", 129.059286687867, 35.152413358569),
                createBusStop("508040000", null, "서면역12번출구", 129.060153513308, 35.158722523902),
                createBusStop("516660000", null, "서면부속상가", 129.060916591770, 35.157652928082),
                createBusStop("164450101", "05277", "서면부속상가", 129.061021125990, 35.157654959269),
                createBusStop("516670000", null, "서면부속상가", 129.061652235481, 35.157888226490),
                createBusStop("506980000", "05365", "서면교차로", 129.061751007201, 35.157888797896),
                createBusStop("164340101", "05253", "NC서면점", 129.063080553076, 35.157671235527),
                createBusStop("213370201", null, "서면자동차학원", 129.072422770913, 35.158366565527)
        );
    }

    @BeforeEach
    void setUp(){
        mockBusStops = createMockBusStops();
    }

    @Nested
    @DisplayName("정류장 이름 검색 테스트")
    class FindByNameTest {

        @Test
        @DisplayName("정류장 이름 완전 일치")
        void findBusStopByExactName(){

            String stopName = "서면한전";
            List<BusStop> expectedStops = mockBusStops.stream()
                    .filter(stop -> stop.getStopName().equals(stopName))
                    .toList();

            when(busStopRepository.findByStopNameContaining(stopName))
                    .thenReturn(expectedStops);

            List<BusStopResponse> result = busStopService.findBusStopsByName(stopName);

            assertThat(result).hasSize(2);
            assertThat(result).allMatch(r -> r.getStopName().equals("서면한전"));
            verify(busStopRepository, times(1)).findByStopNameContaining(stopName);
        }

        @Test
        @DisplayName("존재하지 않는 정류장 이름 검색 시 예외")
        void findBusStopByNotFoundName(){

            String stopName = "없는 정류장";

            when(busStopRepository.findByStopNameContaining(stopName))
                    .thenReturn(Collections.emptyList());

            Assertions.assertThatThrownBy(() -> busStopService.findBusStopsByName(stopName))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode",ErrorCode.BUSSTOP_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("정류장 ID로 검색")
    class FindByIdTest{

        @Test
        @DisplayName("ID 검색 성공")
        void findBusStopById(){

            String stopId = "512450000";
            BusStop expectedStop = mockBusStops.get(0);

            when(busStopRepository.findById(stopId))
                    .thenReturn(Optional.of(expectedStop));

            BusStopResponse result = busStopService.findBusStopByStopId(stopId);

            assertThat(result).isNotNull();
            assertThat(result.getStopId()).isEqualTo(stopId);
            assertThat(result.getStopName()).isEqualTo("e편한세상서면더센트럴");
            assertThat(result.getStopNo()).isEqualTo("05372");

            verify(busStopRepository, times(1)).findById(stopId);
        }

        @Test
        @DisplayName("존재하지 않는 정류장 ID로 검색")
        void findBusStopByInvalidId(){

            String stopId = "000000000";

            when(busStopRepository.findById(stopId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> busStopService.findBusStopByStopId(stopId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BUSSTOP_NOT_FOUND);
        }

        @Test
        @DisplayName("음수 정류장 ID로 검색")
        void findBusStopByNegativeId(){

            String stopId = "-12345678";

            when(busStopRepository.findById(stopId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> busStopService.findBusStopByStopId(stopId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BUSSTOP_NOT_FOUND);
        }

        @Test
        @DisplayName("문자열이 포함된 정류장 ID로 검색")
        void findBusStopByIdWithChar(){

            String stopId = "abcd1234";

            when(busStopRepository.findById(stopId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> busStopService.findBusStopByStopId(stopId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BUSSTOP_NOT_FOUND);
        }

        @Test
        @DisplayName("공백이 포함된 정류장 ID로 검색")
        void findBusStopByIdWithBlank(){

            String stopId = "5124 50000";

            when(busStopRepository.findById(stopId))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> busStopService.findBusStopByStopId(stopId))
                    .isInstanceOf(CustomException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.BUSSTOP_NOT_FOUND);
        }
    }
}