package com.sl_tourpal.backend.config;

import com.sl_tourpal.backend.domain.*;
import com.sl_tourpal.backend.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataLoader implements ApplicationRunner {

    private final PlaceRepository placeRepository;
    private final ActivityRepository activityRepository;
    private final TourRepository tourRepository;

    public DataLoader(PlaceRepository placeRepository, 
                     ActivityRepository activityRepository,
                     TourRepository tourRepository) {
        this.placeRepository = placeRepository;
        this.activityRepository = activityRepository;
        this.tourRepository = tourRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("DataLoader starting...");
        
        long placeCount = placeRepository.count();
        long activityCount = activityRepository.count();
        long tourCount = tourRepository.count();
        
        System.out.println("Current counts - Places: " + placeCount + ", Activities: " + activityCount + ", Tours: " + tourCount);
        
        // Force reseed option - set to true to clear and reseed all data
        boolean forceReseed = true; // Enabled to fix FlywayFixConfig issue
        
        if (forceReseed) {
            System.out.println("Force reseed enabled - clearing existing data...");
            tourRepository.deleteAll();
            activityRepository.deleteAll();
            placeRepository.deleteAll();
            System.out.println("Existing data cleared");
        }
        
        // Only seed if tables are empty
        if (placeRepository.count() == 0) {
            System.out.println("Seeding places...");
            seedPlaces();
        } else {
            System.out.println("Places table already has data, skipping seeding");
        }
        
        if (activityRepository.count() == 0) {
            System.out.println("Seeding activities...");
            seedActivities();
        } else {
            System.out.println("Activities table already has data, skipping seeding");
        }
        
        if (tourRepository.count() == 0) {
            System.out.println("Seeding tours...");
            seedTours();
        } else {
            System.out.println("Tours table already has data, skipping seeding");
        }
        
        System.out.println("DataLoader completed");
    }

    private void seedPlaces() {
        try {
            List<Place> places = Arrays.asList(
                createPlace("Sigiriya Rock Fortress", "North Central Province"),
                createPlace("Temple of the Sacred Tooth Relic", "Central Province"), 
                createPlace("Galle Dutch Fort", "Southern Province"),
                createPlace("Dambulla Cave Temple", "Central Province"),
                createPlace("Yala National Park", "Southern Province"),
                createPlace("Nuwara Eliya Tea Plantations", "Central Province"),
                createPlace("Polonnaruwa Ancient City", "North Central Province"),
                createPlace("Anuradhapura Sacred City", "North Central Province"),
                createPlace("Ella Nine Arch Bridge", "Uva Province"),
                createPlace("Mirissa Beach", "Southern Province")
            );
            
            List<Place> savedPlaces = placeRepository.saveAll(places);
            System.out.println("Successfully seeded " + savedPlaces.size() + " places");
            
            // Verify the save
            long count = placeRepository.count();
            System.out.println("Total places in database after seeding: " + count);
        } catch (Exception e) {
            System.err.println("Error seeding places: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void seedActivities() {
        try {
            List<Activity> activities = Arrays.asList(
                createActivity("Wildlife Safari", "Southern Province"),
                createActivity("Tea Factory Tour", "Central Province"),
                createActivity("Cultural Heritage Tour", "Central Province"),
                createActivity("Beach Activities", "Southern Province"),
                createActivity("Hiking & Trekking", "Central Province"),
                createActivity("Photography Tour", "All Regions"),
                createActivity("Whale Watching", "Southern Province"),
                createActivity("Train Journey", "Central Province"),
                createActivity("Rock Climbing", "North Central Province"),
                createActivity("Spice Garden Visit", "Central Province")
            );
            
            List<Activity> savedActivities = activityRepository.saveAll(activities);
            System.out.println("Successfully seeded " + savedActivities.size() + " activities");
            
            // Verify the save
            long count = activityRepository.count();
            System.out.println("Total activities in database after seeding: " + count);
        } catch (Exception e) {
            System.err.println("Error seeding activities: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void seedTours() {
        try {
            // Create 2 Incomplete tours
            Tour incompleteTour1 = createIncompleteTour1();
            Tour incompleteTour2 = createIncompleteTour2();
            
            // Create 3 Upcoming tours with complete details
            Tour upcomingTour1 = createUpcomingTour1();
            Tour upcomingTour2 = createUpcomingTour2();
            Tour upcomingTour3 = createUpcomingTour3();
            
            List<Tour> tours = Arrays.asList(incompleteTour1, incompleteTour2, upcomingTour1, upcomingTour2, upcomingTour3);
            List<Tour> savedTours = tourRepository.saveAll(tours);
            System.out.println("Successfully seeded " + savedTours.size() + " tours");
            
            // Verify the save
            long count = tourRepository.count();
            System.out.println("Total tours in database after seeding: " + count);
        } catch (Exception e) {
            System.err.println("Error seeding tours: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Tour createIncompleteTour1() {
        Tour tour = new Tour();
        tour.setName("Cultural Triangle Explorer");
        tour.setCategory("Cultural");
        tour.setDurationValue(3);
        tour.setDurationUnit("days");
        tour.setShortDescription("Explore the ancient cultural sites of Sri Lanka");
        tour.setDifficulty("Easy");
        tour.setRegion("North Central Province");
        tour.setStatus("INCOMPLETE");
        tour.setIsCustom(false);
        tour.setAvailableSpots(0);
        tour.setPrice(BigDecimal.ZERO);
        
        // Add basic highlights
        tour.setHighlights(Arrays.asList(
            "Visit Sigiriya Rock Fortress",
            "Explore Dambulla Cave Temple"
        ));
        
        // Add basic activities
        Set<String> activities = new HashSet<>();
        activities.add("Cultural Heritage Tour");
        activities.add("Photography Tour");
        tour.setActivities(activities);
        
        return tour;
    }

    private Tour createIncompleteTour2() {
        Tour tour = new Tour();
        tour.setName("Southern Coast Adventure");
        tour.setCategory("Adventure");
        tour.setDurationValue(2);
        tour.setDurationUnit("days");
        tour.setShortDescription("Experience the beautiful southern coast");
        tour.setDifficulty("Moderate");
        tour.setRegion("Southern Province");
        tour.setStatus("INCOMPLETE");
        tour.setIsCustom(false);
        tour.setAvailableSpots(0);
        tour.setPrice(BigDecimal.ZERO);
        
        // Add basic highlights
        tour.setHighlights(Arrays.asList(
            "Visit Galle Dutch Fort",
            "Relax at Mirissa Beach"
        ));
        
        // Add basic activities
        Set<String> activities = new HashSet<>();
        activities.add("Beach Activities");
        activities.add("Whale Watching");
        tour.setActivities(activities);
        
        return tour;
    }

    private Tour createUpcomingTour1() {
        Tour tour = new Tour();
        tour.setName("Complete Sri Lanka Cultural Journey");
        tour.setCategory("Cultural");
        tour.setDurationValue(7);
        tour.setDurationUnit("days");
        tour.setShortDescription("A comprehensive cultural journey through Sri Lanka's ancient kingdoms and sacred sites");
        tour.setDifficulty("Easy");
        tour.setRegion("Multiple Provinces");
        tour.setStatus("UPCOMING");
        tour.setIsCustom(false);
        tour.setAvailableSpots(15);
        tour.setPrice(new BigDecimal("850.00"));
        
        // Add comprehensive highlights
        tour.setHighlights(Arrays.asList(
            "Climb the iconic Sigiriya Rock Fortress",
            "Explore the ancient Dambulla Cave Temple",
            "Visit the sacred Temple of the Tooth in Kandy",
            "Discover Polonnaruwa's ancient ruins",
            "Experience traditional Sri Lankan culture"
        ));
        
        // Add activities
        Set<String> activities = new HashSet<>();
        activities.add("Cultural Heritage Tour");
        activities.add("Photography Tour");
        activities.add("Hiking & Trekking");
        tour.setActivities(activities);
        
        // Add detailed itinerary
        addItineraryDays(tour, Arrays.asList(
            new ItineraryDayData(1, "Arrival & Sigiriya", "Arrive in Colombo, transfer to Sigiriya, climb the rock fortress", "sigiriya.jpg", new HashSet<>(Arrays.asList("Sigiriya Rock Fortress"))),
            new ItineraryDayData(2, "Dambulla & Polonnaruwa", "Visit Dambulla Cave Temple, explore Polonnaruwa ancient city", "dambulla.jpg", new HashSet<>(Arrays.asList("Dambulla Cave Temple", "Polonnaruwa Ancient City"))),
            new ItineraryDayData(3, "Journey to Kandy", "Travel to Kandy, visit spice garden en route", "kandy.jpg", new HashSet<>(Arrays.asList("Kandy"))),
            new ItineraryDayData(4, "Kandy Exploration", "Temple of the Tooth, Royal Botanical Gardens, cultural show", "tooth-temple.jpg", new HashSet<>(Arrays.asList("Temple of the Sacred Tooth Relic"))),
            new ItineraryDayData(5, "Tea Country", "Journey to Nuwara Eliya, tea plantation tour", "tea.jpg", new HashSet<>(Arrays.asList("Nuwara Eliya Tea Plantations"))),
            new ItineraryDayData(6, "Ella Adventure", "Train to Ella, Nine Arch Bridge, Little Adam's Peak", "ella.jpg", new HashSet<>(Arrays.asList("Ella Nine Arch Bridge"))),
            new ItineraryDayData(7, "Departure", "Return to Colombo for departure", "colombo.jpg", new HashSet<>(Arrays.asList("Colombo")))
        ));
        
        // Add accommodations
        addAccommodations(tour, Arrays.asList(
            new AccommodationData("Sigiriya Heritage Hotel", "Comfortable hotel with views of Sigiriya Rock", Arrays.asList("hotel1.jpg")),
            new AccommodationData("Kandy City Hotel", "Modern hotel in the heart of Kandy", Arrays.asList("hotel2.jpg")),
            new AccommodationData("Nuwara Eliya Tea Estate Bungalow", "Colonial-style bungalow in tea country", Arrays.asList("hotel3.jpg"))
        ));
        
        // Add availability ranges
        addAvailabilityRanges(tour, Arrays.asList(
            new AvailabilityData(LocalDate.now().plusDays(30), LocalDate.now().plusDays(37)),
            new AvailabilityData(LocalDate.now().plusDays(60), LocalDate.now().plusDays(67)),
            new AvailabilityData(LocalDate.now().plusDays(90), LocalDate.now().plusDays(97))
        ));
        
        // Add tour images
        addTourImages(tour, Arrays.asList(
            new TourImageData("cultural-journey-main.jpg", true),
            new TourImageData("sigiriya-view.jpg", false),
            new TourImageData("kandy-temple.jpg", false)
        ));
        
        return tour;
    }

    private Tour createUpcomingTour2() {
        Tour tour = new Tour();
        tour.setName("Wildlife & Beach Safari");
        tour.setCategory("Wildlife");
        tour.setDurationValue(5);
        tour.setDurationUnit("days");
        tour.setShortDescription("Experience Sri Lanka's incredible wildlife and pristine beaches");
        tour.setDifficulty("Moderate");
        tour.setRegion("Southern Province");
        tour.setStatus("UPCOMING");
        tour.setIsCustom(false);
        tour.setAvailableSpots(12);
        tour.setPrice(new BigDecimal("650.00"));
        
        // Add highlights
        tour.setHighlights(Arrays.asList(
            "Safari in Yala National Park",
            "Spot leopards, elephants, and exotic birds",
            "Relax on beautiful southern beaches",
            "Whale watching from Mirissa",
            "Visit historic Galle Fort"
        ));
        
        // Add activities
        Set<String> activities = new HashSet<>();
        activities.add("Wildlife Safari");
        activities.add("Beach Activities");
        activities.add("Whale Watching");
        activities.add("Photography Tour");
        tour.setActivities(activities);
        
        // Add itinerary
        addItineraryDays(tour, Arrays.asList(
            new ItineraryDayData(1, "Arrival & Yala", "Arrive, transfer to Yala, evening safari", "yala.jpg", new HashSet<>(Arrays.asList("Yala National Park"))),
            new ItineraryDayData(2, "Full Day Safari", "Morning and evening safaris in Yala National Park", "safari.jpg", new HashSet<>(Arrays.asList("Yala National Park"))),
            new ItineraryDayData(3, "Galle & Beach", "Travel to Galle, explore Dutch Fort, beach time", "galle.jpg", new HashSet<>(Arrays.asList("Galle Dutch Fort"))),
            new ItineraryDayData(4, "Whale Watching", "Early morning whale watching from Mirissa", "whales.jpg", new HashSet<>(Arrays.asList("Mirissa Beach"))),
            new ItineraryDayData(5, "Departure", "Beach relaxation, departure from Colombo", "beach.jpg", new HashSet<>(Arrays.asList("Colombo")))
        ));
        
        // Add accommodations
        addAccommodations(tour, Arrays.asList(
            new AccommodationData("Yala Safari Lodge", "Eco-friendly lodge near Yala National Park", Arrays.asList("yala-lodge.jpg")),
            new AccommodationData("Galle Heritage Villa", "Boutique hotel within Galle Fort", Arrays.asList("galle-villa.jpg"))
        ));
        
        // Add availability
        addAvailabilityRanges(tour, Arrays.asList(
            new AvailabilityData(LocalDate.now().plusDays(45), LocalDate.now().plusDays(50)),
            new AvailabilityData(LocalDate.now().plusDays(75), LocalDate.now().plusDays(80))
        ));
        
        // Add images
        addTourImages(tour, Arrays.asList(
            new TourImageData("wildlife-safari-main.jpg", true),
            new TourImageData("leopard.jpg", false),
            new TourImageData("beach-sunset.jpg", false)
        ));
        
        return tour;
    }

    private Tour createUpcomingTour3() {
        Tour tour = new Tour();
        tour.setName("Hill Country Tea Trail");
        tour.setCategory("Nature");
        tour.setDurationValue(4);
        tour.setDurationUnit("days");
        tour.setShortDescription("Journey through Sri Lanka's scenic tea country with plantation visits and mountain views");
        tour.setDifficulty("Easy");
        tour.setRegion("Central Province");
        tour.setStatus("UPCOMING");
        tour.setIsCustom(false);
        tour.setAvailableSpots(20);
        tour.setPrice(new BigDecimal("450.00"));
        
        // Add highlights
        tour.setHighlights(Arrays.asList(
            "Scenic train journey through tea country",
            "Visit working tea plantations",
            "Learn about tea production process",
            "Stunning mountain and valley views",
            "Traditional tea tasting sessions"
        ));
        
        // Add activities
        Set<String> activities = new HashSet<>();
        activities.add("Tea Factory Tour");
        activities.add("Train Journey");
        activities.add("Hiking & Trekking");
        activities.add("Photography Tour");
        tour.setActivities(activities);
        
        // Add itinerary
        addItineraryDays(tour, Arrays.asList(
            new ItineraryDayData(1, "Kandy to Nuwara Eliya", "Travel from Kandy to Nuwara Eliya, visit tea factory", "tea-factory.jpg", new HashSet<>(Arrays.asList("Nuwara Eliya Tea Plantations"))),
            new ItineraryDayData(2, "Tea Plantation Tour", "Full day exploring tea plantations and learning about tea production", "plantation.jpg", new HashSet<>(Arrays.asList("Nuwara Eliya Tea Plantations"))),
            new ItineraryDayData(3, "Scenic Train to Ella", "Famous train journey from Nuwara Eliya to Ella", "train.jpg", new HashSet<>(Arrays.asList("Ella Nine Arch Bridge"))),
            new ItineraryDayData(4, "Ella Exploration", "Nine Arch Bridge, Little Adam's Peak, return journey", "ella-bridge.jpg", new HashSet<>(Arrays.asList("Ella Nine Arch Bridge")))
        ));
        
        // Add accommodations
        addAccommodations(tour, Arrays.asList(
            new AccommodationData("Nuwara Eliya Grand Hotel", "Colonial-era hotel in the heart of tea country", Arrays.asList("grand-hotel.jpg")),
            new AccommodationData("Ella Mountain View Lodge", "Cozy lodge with panoramic mountain views", Arrays.asList("ella-lodge.jpg"))
        ));
        
        // Add availability
        addAvailabilityRanges(tour, Arrays.asList(
            new AvailabilityData(LocalDate.now().plusDays(20), LocalDate.now().plusDays(24)),
            new AvailabilityData(LocalDate.now().plusDays(50), LocalDate.now().plusDays(54)),
            new AvailabilityData(LocalDate.now().plusDays(80), LocalDate.now().plusDays(84))
        ));
        
        // Add images
        addTourImages(tour, Arrays.asList(
            new TourImageData("tea-trail-main.jpg", true),
            new TourImageData("tea-picker.jpg", false),
            new TourImageData("mountain-view.jpg", false)
        ));
        
        return tour;
    }

    // Helper methods to add related entities
    private void addItineraryDays(Tour tour, List<ItineraryDayData> dayDataList) {
        for (ItineraryDayData data : dayDataList) {
            ItineraryDay day = new ItineraryDay();
            day.setDayNumber(data.dayNumber);
            day.setTitle(data.title);
            day.setDescription(data.description);
            day.setImageUrl(data.imageUrl);
            day.setDestinations(data.destinations);
            day.setTour(tour);
            tour.getItineraryDays().add(day);
        }
    }

    private void addAccommodations(Tour tour, List<AccommodationData> accommodationDataList) {
        for (AccommodationData data : accommodationDataList) {
            Accommodation accommodation = new Accommodation();
            accommodation.setTitle(data.title);
            accommodation.setDescription(data.description);
            accommodation.setImages(data.images);
            accommodation.setTour(tour);
            tour.getAccommodations().add(accommodation);
        }
    }

    private void addAvailabilityRanges(Tour tour, List<AvailabilityData> availabilityDataList) {
        for (AvailabilityData data : availabilityDataList) {
            AvailabilityRange range = new AvailabilityRange();
            range.setStartDate(data.startDate);
            range.setEndDate(data.endDate);
            range.setTour(tour);
            tour.getAvailabilityRanges().add(range);
        }
    }

    private void addTourImages(Tour tour, List<TourImageData> imageDataList) {
        for (TourImageData data : imageDataList) {
            TourImage image = new TourImage();
            image.setUrl(data.url);
            image.setPrimary(data.isPrimary);
            image.setTour(tour);
            tour.getImages().add(image);
        }
    }

    // Helper data classes
    private static class ItineraryDayData {
        final int dayNumber;
        final String title;
        final String description;
        final String imageUrl;
        final Set<String> destinations;

        ItineraryDayData(int dayNumber, String title, String description, String imageUrl, Set<String> destinations) {
            this.dayNumber = dayNumber;
            this.title = title;
            this.description = description;
            this.imageUrl = imageUrl;
            this.destinations = destinations;
        }
    }

    private static class AccommodationData {
        final String title;
        final String description;
        final List<String> images;

        AccommodationData(String title, String description, List<String> images) {
            this.title = title;
            this.description = description;
            this.images = images;
        }
    }

    private Place createPlace(String name, String region) {
        Place place = new Place();
        place.setName(name);
        place.setRegion(region);
        return place;
    }

    private Activity createActivity(String name, String region) {
        Activity activity = new Activity();
        activity.setName(name);
        activity.setRegion(region);
        return activity;
    }

    private static class AvailabilityData {
        final LocalDate startDate;
        final LocalDate endDate;

        AvailabilityData(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }

    private static class TourImageData {
        final String url;
        final boolean isPrimary;

        TourImageData(String url, boolean isPrimary) {
            this.url = url;
            this.isPrimary = isPrimary;
        }
    }
}
