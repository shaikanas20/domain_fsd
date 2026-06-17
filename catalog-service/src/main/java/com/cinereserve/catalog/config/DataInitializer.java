package com.cinereserve.catalog.config;

import com.cinereserve.catalog.model.Location;
import com.cinereserve.catalog.model.Movie;
import com.cinereserve.catalog.model.SeatLayout;
import com.cinereserve.catalog.model.SeatLayout.SeatConfig;
import com.cinereserve.catalog.model.Theatre;
import com.cinereserve.catalog.model.Screen;
import com.cinereserve.catalog.repository.LocationRepository;
import com.cinereserve.catalog.repository.MovieRepository;
import com.cinereserve.catalog.repository.SeatLayoutRepository;
import com.cinereserve.catalog.repository.TheatreRepository;
import com.cinereserve.catalog.repository.ScreenRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final LocationRepository locationRepository;
    private final MovieRepository movieRepository;
    private final SeatLayoutRepository seatLayoutRepository;
    private final TheatreRepository theatreRepository;
    private final ScreenRepository screenRepository;

    public DataInitializer(
            LocationRepository locationRepository,
            MovieRepository movieRepository,
            SeatLayoutRepository seatLayoutRepository,
            TheatreRepository theatreRepository,
            ScreenRepository screenRepository) {
        this.locationRepository = locationRepository;
        this.movieRepository = movieRepository;
        this.seatLayoutRepository = seatLayoutRepository;
        this.theatreRepository = theatreRepository;
        this.screenRepository = screenRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        long movieCount = movieRepository.count();

        // Reseed movies if we have fewer than the full catalog of 42
        if (movieCount < 42) {
            // Clear and re-seed movies
            movieRepository.deleteAll();
            System.out.println(">>> Seeding full movie catalog (42 movies) into MongoDB...");

        // ─── ORIGINAL 12 MOVIES ───────────────────────────────────────────────────
        List<Movie> movies = new ArrayList<>(Arrays.asList(
            // ── NOW SHOWING ──
            Movie.builder()
                .title("Jawan")
                .description("A high-octane action thriller that outlines the emotional journey of a man who is set to rectify the wrongs in society.")
                .genres(Arrays.asList("Action", "Thriller"))
                .duration(168)
                .rating(8.4)
                .languages(Arrays.asList("Hindi", "Tamil", "Telugu"))
                .releaseDate(LocalDate.of(2023, 9, 7))
                .poster("https://image.tmdb.org/t/p/w500/kuxpwOwN1GZz2q9N8Y4C1t4Vf9O.jpg")
                .trailer("https://www.youtube.com/watch?v=COv527yI-54")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Pathaan")
                .description("An Indian spy agent is sent to stop a private terror group planning a major strike against the country.")
                .genres(Arrays.asList("Action", "Adventure", "Thriller"))
                .duration(146)
                .rating(7.9)
                .languages(Arrays.asList("Hindi", "Tamil", "Telugu"))
                .releaseDate(LocalDate.of(2023, 1, 25))
                .poster("https://image.tmdb.org/t/p/w500/wwemzKWKqIZC30y5b28Kx7qj5Yq.jpg")
                .trailer("https://www.youtube.com/watch?v=vqu4z34wENw")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("K.G.F: Chapter 2")
                .description("In the blood-drenched Kolar Gold Fields, Rocky's name strikes fear into his foes while his allies look up to him.")
                .genres(Arrays.asList("Action", "Drama"))
                .duration(168)
                .rating(8.5)
                .languages(Arrays.asList("Kannada", "Hindi", "Telugu", "Tamil", "Malayalam"))
                .releaseDate(LocalDate.of(2022, 4, 14))
                .poster("https://image.tmdb.org/t/p/w500/d5N79bO22mQAPQcU3TSZroFh42l.jpg")
                .trailer("https://www.youtube.com/watch?v=JKa05nyUj6U")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("RRR")
                .description("A fictitious story about two legendary revolutionaries and their journey away from home before they started fighting for their country in the 1920s.")
                .genres(Arrays.asList("Action", "Drama"))
                .duration(187)
                .rating(8.8)
                .languages(Arrays.asList("Telugu", "Hindi", "Tamil", "Kannada", "Malayalam"))
                .releaseDate(LocalDate.of(2022, 3, 25))
                .poster("https://image.tmdb.org/t/p/w500/mQBz0kkJw9gWW1accn1UIPVnvtL.jpg")
                .trailer("https://www.youtube.com/watch?v=NgBoMJy386M")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Kantara")
                .description("When greed paves the way for betrayal, scheming and rebellion, a young man reluctantly takes up the mantle of his ancestors to protect his village.")
                .genres(Arrays.asList("Action", "Drama", "Mystery"))
                .duration(150)
                .rating(8.3)
                .languages(Arrays.asList("Kannada", "Hindi", "Telugu", "Tamil"))
                .releaseDate(LocalDate.of(2022, 9, 30))
                .poster("https://image.tmdb.org/t/p/w500/g51tJgV3G85o19k7l47w8lD6t5D.jpg")
                .trailer("https://www.youtube.com/watch?v=8QyZ7sRk10k")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Pushpa: The Rise")
                .description("Violence erupts between red sandalwood smugglers and the police charged with bringing down their organization.")
                .genres(Arrays.asList("Action", "Drama", "Thriller"))
                .duration(179)
                .rating(8.1)
                .languages(Arrays.asList("Telugu", "Tamil", "Hindi", "Malayalam", "Kannada"))
                .releaseDate(LocalDate.of(2021, 12, 17))
                .poster("https://image.tmdb.org/t/p/w500/i0bBAvXjeunGjImqd4HOXhZPwKa.jpg")
                .trailer("https://www.youtube.com/watch?v=Q1NKMPhP8PY")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Baahubali 2: The Conclusion")
                .description("When Shiva, the son of Bahubali, learns about his heritage, he begins to look for answers.")
                .genres(Arrays.asList("Action", "Drama", "Fantasy"))
                .duration(167)
                .rating(9.0)
                .languages(Arrays.asList("Telugu", "Tamil", "Hindi", "Malayalam"))
                .releaseDate(LocalDate.of(2017, 4, 28))
                .poster("https://image.tmdb.org/t/p/w500/bW0LgU29f632z5079aQx9S52T94.jpg")
                .trailer("https://www.youtube.com/watch?v=G62HrubdD6o")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Dangal")
                .description("Former wrestler Mahavir Singh Phogat and his two wrestler daughters struggle towards glory at the Commonwealth Games.")
                .genres(Arrays.asList("Biography", "Drama", "Sport"))
                .duration(161)
                .rating(8.9)
                .languages(Arrays.asList("Hindi", "Tamil", "Telugu"))
                .releaseDate(LocalDate.of(2016, 12, 23))
                .poster("https://image.tmdb.org/t/p/w500/p2lVAcPuRPSO8Al6hDDGw0OgMi8.jpg")
                .trailer("https://www.youtube.com/watch?v=x_7YlGv9u1g")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Kalki 2898 AD")
                .description("A modern avatar of Vishnu is believed to have descended to earth to protect the world from evil forces in a dystopian future.")
                .genres(Arrays.asList("Action", "Sci-Fi"))
                .duration(180)
                .rating(8.2)
                .languages(Arrays.asList("Telugu", "Hindi", "Tamil", "Malayalam", "Kannada"))
                .releaseDate(LocalDate.of(2024, 6, 27))
                .poster("https://image.tmdb.org/t/p/w500/g611sC17d6s9G4pTqZzJjYkQ84v.jpg")
                .trailer("https://www.youtube.com/watch?v=kQDd1AhGIHk")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Stree 2")
                .description("The town of Chanderi is being haunted once again. This time by a headless entity who is abducting independent women.")
                .genres(Arrays.asList("Comedy", "Horror"))
                .duration(147)
                .rating(8.0)
                .languages(Arrays.asList("Hindi"))
                .releaseDate(LocalDate.of(2024, 8, 15))
                .poster("https://image.tmdb.org/t/p/w500/4j0PNHkMr5ax3IA8tjtxcmPU3QT.jpg")
                .trailer("https://www.youtube.com/watch?v=KVnheGHRRLw")
                .status("NOW_SHOWING")
                .build(),
            // ── UPCOMING ──
            Movie.builder()
                .title("Leo")
                .description("A mild-mannered cafe owner becomes a local hero, but old enemies resurface to challenge him and question his true identity.")
                .genres(Arrays.asList("Action", "Drama", "Thriller"))
                .duration(164)
                .rating(8.0)
                .languages(Arrays.asList("Tamil", "Telugu", "Hindi", "Kannada"))
                .releaseDate(LocalDate.of(2023, 10, 19))
                .poster("https://image.tmdb.org/t/p/w500/i61m1b4g5nJ6YxR64Jb6VbZ46Yk.jpg")
                .trailer("https://www.youtube.com/watch?v=Po3jJhY-Xf4")
                .status("UPCOMING")
                .build(),
            Movie.builder()
                .title("Animal")
                .description("A son's love for his father is tested when a complex feud erupts between rival clans, sparking a cycle of bloodletting and revenge.")
                .genres(Arrays.asList("Action", "Drama", "Crime"))
                .duration(201)
                .rating(7.8)
                .languages(Arrays.asList("Hindi", "Telugu", "Tamil", "Kannada", "Malayalam"))
                .releaseDate(LocalDate.of(2023, 12, 1))
                .poster("https://image.tmdb.org/t/p/w500/oHtoFKMHzEKCnFGvIaJkZAeD6cS.jpg")
                .trailer("https://www.youtube.com/watch?v=Dydmpob7nNL")
                .status("UPCOMING")
                .build(),

            // ─── NEW 15 NOW_SHOWING MOVIES ────────────────────────────────────────────
            Movie.builder()
                .title("Oppenheimer")
                .description("The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb during World War II.")
                .genres(Arrays.asList("Biography", "Drama", "History"))
                .duration(180)
                .rating(8.9)
                .languages(Arrays.asList("English"))
                .releaseDate(LocalDate.of(2023, 7, 21))
                .poster("https://image.tmdb.org/t/p/w500/8Gxv8giaFIzmzdempDvejsVOFbl.jpg")
                .trailer("https://www.youtube.com/watch?v=uYPbbksJxIg")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Barbie")
                .description("Barbie and Ken are having the time of their lives in the colorful and seemingly perfect world of Barbie Land. But when they venture into the real world, Barbie faces a life-changing journey.")
                .genres(Arrays.asList("Comedy", "Fantasy", "Adventure"))
                .duration(114)
                .rating(7.0)
                .languages(Arrays.asList("English"))
                .releaseDate(LocalDate.of(2023, 7, 21))
                .poster("https://image.tmdb.org/t/p/w500/iuFNMS8vlbLGhsUDUPikbXwuUHb.jpg")
                .trailer("https://www.youtube.com/watch?v=pBk4NYhWNMM")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Spider-Man: Across the Spider-Verse")
                .description("Miles Morales catapults across the Multiverse, where he encounters a team of Spider-People charged with protecting its very existence.")
                .genres(Arrays.asList("Animation", "Action", "Adventure"))
                .duration(140)
                .rating(8.7)
                .languages(Arrays.asList("English", "Hindi"))
                .releaseDate(LocalDate.of(2023, 6, 2))
                .poster("https://image.tmdb.org/t/p/w500/8Vt6mWEReuy4Of61Lnj5Xj704m8.jpg")
                .trailer("https://www.youtube.com/watch?v=cqGjhVmlMDs")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Guardians of the Galaxy Vol. 3")
                .description("Still reeling from the loss of Gamora, Peter Quill rallies his team to defend the universe and protect one of their own.")
                .genres(Arrays.asList("Action", "Sci-Fi", "Comedy"))
                .duration(150)
                .rating(7.9)
                .languages(Arrays.asList("English", "Hindi"))
                .releaseDate(LocalDate.of(2023, 5, 5))
                .poster("https://image.tmdb.org/t/p/w500/r2J02Z2OpNTctfOSN1Ydgii51I3.jpg")
                .trailer("https://www.youtube.com/watch?v=u3V5KDHRQvk")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("John Wick: Chapter 4")
                .description("John Wick uncovers a path to defeating The High Table, but before he can earn his freedom, he must face a new enemy with powerful alliances.")
                .genres(Arrays.asList("Action", "Thriller", "Crime"))
                .duration(169)
                .rating(7.8)
                .languages(Arrays.asList("English", "Hindi"))
                .releaseDate(LocalDate.of(2023, 3, 24))
                .poster("https://image.tmdb.org/t/p/w500/vZloFAK7NmvMGKE7VkF5UHaz0I.jpg")
                .trailer("https://www.youtube.com/watch?v=qEVUtrk8_B4")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Top Gun: Maverick")
                .description("After more than thirty years of service as a top naval aviator, Pete Mitchell is back where he belongs, pushing the envelope as a courageous test pilot.")
                .genres(Arrays.asList("Action", "Drama"))
                .duration(130)
                .rating(8.3)
                .languages(Arrays.asList("English", "Hindi"))
                .releaseDate(LocalDate.of(2022, 5, 27))
                .poster("https://image.tmdb.org/t/p/w500/62HCnUTziyWcpDaBO2i1DX17ljH.jpg")
                .trailer("https://www.youtube.com/watch?v=giXco2jaZ_4")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("The Batman")
                .description("Batman ventures into Gotham City's underworld when a sadistic killer leaves behind a trail of cryptic clues targeting Gotham's elite.")
                .genres(Arrays.asList("Action", "Crime", "Drama"))
                .duration(176)
                .rating(7.9)
                .languages(Arrays.asList("English", "Hindi"))
                .releaseDate(LocalDate.of(2022, 3, 4))
                .poster("https://image.tmdb.org/t/p/w500/74xTEgt7R36Fpocon6TC5v016OR.jpg")
                .trailer("https://www.youtube.com/watch?v=mqqft2x_Aa4")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Dune: Part Two")
                .description("Paul Atreides unites with Chani and the Fremen while on a path of revenge against the conspirators who destroyed his family.")
                .genres(Arrays.asList("Action", "Adventure", "Sci-Fi"))
                .duration(166)
                .rating(8.5)
                .languages(Arrays.asList("English", "Hindi"))
                .releaseDate(LocalDate.of(2024, 3, 1))
                .poster("https://image.tmdb.org/t/p/w500/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg")
                .trailer("https://www.youtube.com/watch?v=Way9Dexny3w")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Salaar: Part 1 – Ceasefire")
                .description("A peace-loving man is forced to take up arms again when his friend seeks help to claim his rightful place as the leader of a deadly empire.")
                .genres(Arrays.asList("Action", "Crime", "Thriller"))
                .duration(173)
                .rating(7.5)
                .languages(Arrays.asList("Telugu", "Kannada", "Hindi", "Tamil", "Malayalam"))
                .releaseDate(LocalDate.of(2023, 12, 22))
                .poster("https://image.tmdb.org/t/p/w500/rMvPXy8PUjj1o8o1pzgQbdNCsvj.jpg")
                .trailer("https://www.youtube.com/watch?v=NWqnwQJzaVY")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("HanuMan")
                .description("A young man from a fictional village acquires superpowers from Lord Hanuman to save his village from a deadly villain.")
                .genres(Arrays.asList("Action", "Fantasy", "Superhero"))
                .duration(158)
                .rating(8.4)
                .languages(Arrays.asList("Telugu", "Hindi", "Tamil", "Malayalam", "Kannada"))
                .releaseDate(LocalDate.of(2024, 1, 12))
                .poster("https://image.tmdb.org/t/p/w500/5wDDFRnGNIcB5BNe4HROv8jnRhD.jpg")
                .trailer("https://www.youtube.com/watch?v=MqFnKqcb3G0")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Dunki")
                .description("Four friends take the illegal immigration route called Donkey Flight to reach England, but their journey tests everything they have.")
                .genres(Arrays.asList("Drama", "Comedy", "Adventure"))
                .duration(161)
                .rating(7.4)
                .languages(Arrays.asList("Hindi"))
                .releaseDate(LocalDate.of(2023, 12, 21))
                .poster("https://image.tmdb.org/t/p/w500/5fhZdwP1DVJ0FyVH6vrFdHwzXB8.jpg")
                .trailer("https://www.youtube.com/watch?v=vgqSMBUYFGQ")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Tiger 3")
                .description("Tiger must fight a battle for peace as an enemy from his past returns to destroy him and threatens national security.")
                .genres(Arrays.asList("Action", "Thriller", "Spy"))
                .duration(144)
                .rating(7.5)
                .languages(Arrays.asList("Hindi", "Tamil", "Telugu"))
                .releaseDate(LocalDate.of(2023, 11, 12))
                .poster("https://image.tmdb.org/t/p/w500/8bRiOgkahXFHXS8q7XwFtWfDGGN.jpg")
                .trailer("https://www.youtube.com/watch?v=zS6G-cTdcC8")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Fighter")
                .description("India's first aerial action franchise follows the Indian Air Force as they face a deadly terrorist threat.")
                .genres(Arrays.asList("Action", "Drama", "War"))
                .duration(166)
                .rating(7.2)
                .languages(Arrays.asList("Hindi"))
                .releaseDate(LocalDate.of(2024, 1, 25))
                .poster("https://image.tmdb.org/t/p/w500/aBxAnGlhL3JoWFfcaGUbN7n3tVK.jpg")
                .trailer("https://www.youtube.com/watch?v=IGLtS-lIBhk")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Sam Bahadur")
                .description("The life story of Field Marshal Sam Manekshaw, the first officer of the Indian Army to rise to the rank of Field Marshal.")
                .genres(Arrays.asList("Biography", "Drama", "War"))
                .duration(155)
                .rating(8.2)
                .languages(Arrays.asList("Hindi"))
                .releaseDate(LocalDate.of(2023, 12, 1))
                .poster("https://image.tmdb.org/t/p/w500/5S1RFlH9bDGy8wl3hUh4A08LYVJ.jpg")
                .trailer("https://www.youtube.com/watch?v=SKR70bpKJxs")
                .status("NOW_SHOWING")
                .build(),
            Movie.builder()
                .title("Bhool Bhulaiyaa 3")
                .description("Rooh Baba returns, and this time he encounters the real Manjulika as the mystery deepens in this horror comedy sequel.")
                .genres(Arrays.asList("Horror", "Comedy", "Thriller"))
                .duration(143)
                .rating(7.6)
                .languages(Arrays.asList("Hindi"))
                .releaseDate(LocalDate.of(2024, 11, 1))
                .poster("https://image.tmdb.org/t/p/w500/9NkxTGE8Zn1ztP2JfSBXnBg6MYj.jpg")
                .trailer("https://www.youtube.com/watch?v=gILcRCpEQtA")
                .status("NOW_SHOWING")
                .build(),

            // ─── NEW 15 UPCOMING MOVIES ───────────────────────────────────────────────
            Movie.builder()
                .title("Avatar: Fire and Ash")
                .description("Jake Sully and Neytiri face a new threat as the fire Ash People attack Pandora, forcing the Na'vi into an epic battle to protect their world.")
                .genres(Arrays.asList("Action", "Sci-Fi", "Adventure"))
                .duration(180)
                .rating(0.0)
                .languages(Arrays.asList("English", "Hindi"))
                .releaseDate(LocalDate.of(2025, 12, 19))
                .poster("https://image.tmdb.org/t/p/w500/oiGkT9Ks7hUBmCHCQfBfQVeMmFz.jpg")
                .trailer("https://www.youtube.com/watch?v=fkP5GBpMXRY")
                .status("UPCOMING")
                .build(),
            Movie.builder()
                .title("Mission: Impossible – The Final Reckoning")
                .description("Ethan Hunt faces the most dangerous mission of his career as he confronts the AI entity known as the Entity in a globe-trotting finale.")
                .genres(Arrays.asList("Action", "Thriller", "Spy"))
                .duration(169)
                .rating(8.1)
                .languages(Arrays.asList("English", "Hindi"))
                .releaseDate(LocalDate.of(2025, 5, 23))
                .poster("https://image.tmdb.org/t/p/w500/z53D72EAOxGRqdr7KXXWp9dJiDe.jpg")
                .trailer("https://www.youtube.com/watch?v=avz06PDqDbM")
                .status("UPCOMING")
                .build(),
            Movie.builder()
                .title("Jurassic World: Rebirth")
                .description("Five years after the events of Jurassic World Dominion, a team ventures into a remote island to collect genetic material from three massive dinosaurs.")
                .genres(Arrays.asList("Action", "Adventure", "Sci-Fi"))
                .duration(130)
                .rating(0.0)
                .languages(Arrays.asList("English", "Hindi"))
                .releaseDate(LocalDate.of(2025, 7, 2))
                .poster("https://image.tmdb.org/t/p/w500/9lJ0dSJbIKbCuDLGDMRJOSWbKHu.jpg")
                .trailer("https://www.youtube.com/watch?v=xJoLnbfPNYY")
                .status("UPCOMING")
                .build(),
            Movie.builder()
                .title("The Fantastic Four: First Steps")
                .description("Marvel's First Family ventures into a retro-futuristic world to face Galactus, the Devourer of Worlds, in their first big screen MCU adventure.")
                .genres(Arrays.asList("Action", "Sci-Fi", "Adventure"))
                .duration(125)
                .rating(0.0)
                .languages(Arrays.asList("English", "Hindi"))
                .releaseDate(LocalDate.of(2025, 7, 25))
                .poster("https://image.tmdb.org/t/p/w500/9HoHKtpCJ9mRx6l1OW0XbcfJRqJ.jpg")
                .trailer("https://www.youtube.com/watch?v=V8FLi-B1Pv0")
                .status("UPCOMING")
                .build(),
            Movie.builder()
                .title("Superman")
                .description("Clark Kent / Superman, a reporter in Metropolis, embarks on his journey to reconcile his Kryptonian heritage with his human upbringing.")
                .genres(Arrays.asList("Action", "Sci-Fi", "Adventure"))
                .duration(130)
                .rating(0.0)
                .languages(Arrays.asList("English", "Hindi"))
                .releaseDate(LocalDate.of(2025, 7, 11))
                .poster("https://image.tmdb.org/t/p/w500/aSBaBOVrjUHr2hOtW1LYj5b5H46.jpg")
                .trailer("https://www.youtube.com/watch?v=zO10FZ0NaAY")
                .status("UPCOMING")
                .build(),
            Movie.builder()
                .title("War 2")
                .description("Agent Kabir Dhaliwal returns in this high-octane spy thriller where he faces a formidable new adversary threatening global stability.")
                .genres(Arrays.asList("Action", "Thriller", "Spy"))
                .duration(150)
                .rating(0.0)
                .languages(Arrays.asList("Hindi", "Tamil", "Telugu"))
                .releaseDate(LocalDate.of(2025, 8, 14))
                .poster("https://image.tmdb.org/t/p/w500/qlHALMBnvRkn0h7e0eaU6l5qY2v.jpg")
                .trailer("https://www.youtube.com/watch?v=S48CY5R0MoE")
                .status("UPCOMING")
                .build(),
            Movie.builder()
                .title("Sikandar")
                .description("A powerful man with a mysterious past emerges to fight for justice, confronting enemies on multiple fronts in this epic action drama.")
                .genres(Arrays.asList("Action", "Drama"))
                .duration(165)
                .rating(0.0)
                .languages(Arrays.asList("Hindi", "Tamil", "Telugu"))
                .releaseDate(LocalDate.of(2025, 3, 30))
                .poster("https://image.tmdb.org/t/p/w500/3JpfGt1qT76y9bxPsIHXfsPCO5A.jpg")
                .trailer("https://www.youtube.com/watch?v=mQK5srlFRPc")
                .status("UPCOMING")
                .build(),
            Movie.builder()
                .title("Coolie")
                .description("A baggage handler at a railway station with a dark secret takes on a powerful criminal empire to protect the innocent.")
                .genres(Arrays.asList("Action", "Thriller"))
                .duration(160)
                .rating(0.0)
                .languages(Arrays.asList("Tamil", "Telugu", "Hindi", "Malayalam", "Kannada"))
                .releaseDate(LocalDate.of(2025, 5, 1))
                .poster("https://image.tmdb.org/t/p/w500/eTnUYT9Aoy46sDEMDXM1e1YVp3G.jpg")
                .trailer("https://www.youtube.com/watch?v=mxFcFVEpXFQ")
                .status("UPCOMING")
                .build(),
            Movie.builder()
                .title("Retro")
                .description("Set across two eras, Retro explores a complex love story between two people whose destinies are intertwined through decades of choices.")
                .genres(Arrays.asList("Romance", "Drama", "Thriller"))
                .duration(155)
                .rating(0.0)
                .languages(Arrays.asList("Tamil", "Telugu", "Hindi"))
                .releaseDate(LocalDate.of(2025, 8, 15))
                .poster("https://image.tmdb.org/t/p/w500/kJi9fVAhSJHbkdxiqOGZnwFiQ5Z.jpg")
                .trailer("https://www.youtube.com/watch?v=3Zn3R1MJKZ8")
                .status("UPCOMING")
                .build(),
            Movie.builder()
                .title("Toxic")
                .description("A stylish gangster story about a man who builds a criminal empire and must confront the consequences of a life built on power and violence.")
                .genres(Arrays.asList("Action", "Crime", "Thriller"))
                .duration(158)
                .rating(0.0)
                .languages(Arrays.asList("Kannada", "Hindi", "Tamil", "Telugu"))
                .releaseDate(LocalDate.of(2025, 4, 10))
                .poster("https://image.tmdb.org/t/p/w500/dLzmzFJaEhKlpIzfmixUOkRpXDN.jpg")
                .trailer("https://www.youtube.com/watch?v=lQ0OjEPuH3I")
                .status("UPCOMING")
                .build(),
            Movie.builder()
                .title("The Raja Saab")
                .description("A horror comedy about a charming young man who falls for a ghost, leading to a series of supernatural and comedic misadventures.")
                .genres(Arrays.asList("Horror", "Comedy", "Romance"))
                .duration(150)
                .rating(0.0)
                .languages(Arrays.asList("Telugu", "Hindi", "Tamil"))
                .releaseDate(LocalDate.of(2025, 4, 10))
                .poster("https://image.tmdb.org/t/p/w500/wBqXJBuCZdS4l2IUjq4fY8dqBwH.jpg")
                .trailer("https://www.youtube.com/watch?v=5dvREMV9TBo")
                .status("UPCOMING")
                .build(),
            Movie.builder()
                .title("Raid 2")
                .description("Amay Patnaik returns as an incorruptible IRS officer who takes on a ruthless nexus of politicians and criminals in a high-stakes investigation.")
                .genres(Arrays.asList("Action", "Crime", "Thriller"))
                .duration(148)
                .rating(0.0)
                .languages(Arrays.asList("Hindi"))
                .releaseDate(LocalDate.of(2025, 5, 1))
                .poster("https://image.tmdb.org/t/p/w500/KR6JcBhsKfmm5EuJfvQ6rLJOIqw.jpg")
                .trailer("https://www.youtube.com/watch?v=FquCFSjEBXQ")
                .status("UPCOMING")
                .build(),
            Movie.builder()
                .title("Chhaava")
                .description("The story of Chhatrapati Sambhaji Maharaj, the valiant son of Chhatrapati Shivaji Maharaj, and his battles to protect the Maratha Empire.")
                .genres(Arrays.asList("Biography", "Action", "Historical"))
                .duration(160)
                .rating(8.5)
                .languages(Arrays.asList("Hindi", "Marathi"))
                .releaseDate(LocalDate.of(2025, 2, 14))
                .poster("https://image.tmdb.org/t/p/w500/5nwuzGNIqZ0VRfmovKXC1oPRXfD.jpg")
                .trailer("https://www.youtube.com/watch?v=l1fPV0E3n80")
                .status("UPCOMING")
                .build(),
            Movie.builder()
                .title("Sky Force")
                .description("Based on India's first air strike, this film recreates the audacious 1965 Sargodha air operation by the Indian Air Force.")
                .genres(Arrays.asList("Action", "War", "Drama"))
                .duration(145)
                .rating(8.0)
                .languages(Arrays.asList("Hindi"))
                .releaseDate(LocalDate.of(2025, 1, 24))
                .poster("https://image.tmdb.org/t/p/w500/sEby7oFzaqKm7RY5HxhOmZEFMle.jpg")
                .trailer("https://www.youtube.com/watch?v=HFE1oyVGV5Q")
                .status("UPCOMING")
                .build(),
            Movie.builder()
                .title("Kesari Chapter 2")
                .description("The trial of a brave Indian lawyer who fights against colonial injustice in British-era India, inspired by true events.")
                .genres(Arrays.asList("Biography", "Drama", "Legal"))
                .duration(150)
                .rating(0.0)
                .languages(Arrays.asList("Hindi"))
                .releaseDate(LocalDate.of(2025, 4, 18))
                .poster("https://image.tmdb.org/t/p/w500/mNexbGnCq5bKefg3iX0c3DFGdp7.jpg")
                .trailer("https://www.youtube.com/watch?v=SLkRAJFkPXg")
                .status("UPCOMING")
                .build()
        ));

        movieRepository.saveAll(movies);
        System.out.println(">>> Seeded " + movies.size() + " movies into MongoDB.");
        } else {
            System.out.println(">>> Full catalog already seeded (" + movieCount + " movies). Skipping movie seed.");
        }

        // ─── Seed infrastructure ──────────────────────────────────────────────────
        // Always ensure locations exist
        List<Location> savedLocations;
        if (locationRepository.count() == 0) {
            List<Location> locations = Arrays.asList(
                Location.builder().name("Mumbai Central").city("Mumbai").state("Maharashtra").build(),
                Location.builder().name("Delhi NCR").city("Delhi").state("Delhi").build(),
                Location.builder().name("Bengaluru South").city("Bengaluru").state("Karnataka").build(),
                Location.builder().name("Hyderabad Gachibowli").city("Hyderabad").state("Telangana").build(),
                Location.builder().name("Chennai OMR").city("Chennai").state("Tamil Nadu").build(),
                Location.builder().name("Kolkata Salt Lake").city("Kolkata").state("West Bengal").build(),
                Location.builder().name("Pune Koregaon Park").city("Pune").state("Maharashtra").build(),
                Location.builder().name("Ahmedabad SG Road").city("Ahmedabad").state("Gujarat").build(),
                Location.builder().name("Jaipur Pink City").city("Jaipur").state("Rajasthan").build(),
                Location.builder().name("Kochi Harbour").city("Kochi").state("Kerala").build(),
                Location.builder().name("Goa Panaji").city("Goa").state("Goa").build(),
                Location.builder().name("Lucknow Hazratganj").city("Lucknow").state("Uttar Pradesh").build()
            );
            savedLocations = locationRepository.saveAll(locations);
            System.out.println(">>> Seeded 12 locations.");
        } else {
            savedLocations = locationRepository.findAll();
            System.out.println(">>> Locations already exist (" + savedLocations.size() + "). Skipping location seed.");
        }

        // Seat layout — create if missing
        SeatLayout savedLayout;
        if (seatLayoutRepository.count() == 0) {
            List<String> rowNames = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
            List<SeatConfig> seats = new ArrayList<>();
            for (String row : rowNames) {
                for (int col = 1; col <= 12; col++) {
                    String type = (row.equals("A") || row.equals("B")) ? "VIP" : "NORMAL";
                    seats.add(SeatConfig.builder().rowName(row).colIndex(col).seatType(type).build());
                }
            }
            savedLayout = seatLayoutRepository.save(SeatLayout.builder()
                    .name("Standard 120 Seat Hall")
                    .rowNames(rowNames)
                    .colCount(12)
                    .seats(seats)
                    .build());
            System.out.println(">>> Seeded default seat layout.");
        } else {
            savedLayout = seatLayoutRepository.findAll().get(0);
            System.out.println(">>> Seat layout already exists. Skipping.");
        }

        // Theatres — seed 10 APPROVED theatres if fewer than 10 exist
        if (theatreRepository.count() < 10) {
            theatreRepository.deleteAll();
            screenRepository.deleteAll();

            // Build: [locationIndex, name, address, screen1, screen2]
            String[][] theatreData = {
                { "0", "PVR Cinemas Phoenix Mall",       "PVR Phenix Mall, Lower Parel, Mumbai",          "Audi 1 (Dolby Atmos)",   "Audi 2 (4DX)"          },
                { "1", "INOX Connaught Place",           "Odeon Cineplex, Block N, Connaught Place, Delhi","Screen 1 (IMAX)",        "Screen 2 (VIP)"        },
                { "2", "PVR Orion Mall Bengaluru",       "Orion Mall, Dr. Rajkumar Rd, Rajajinagar, Bengaluru", "Audi 1 (Dolby)",  "Audi 2 (Standard)"     },
                { "3", "AMB Cinemas Hyderabad",          "AMB Mall, Gachibowli, Hyderabad",               "Screen 1 (4K Laser)",    "Screen 2 (VIP Lounge)" },
                { "4", "SPI Cinemas Chennai",            "Palazzo Mall, OMR, Chennai",                    "Audi 1 (Dolby Atmos)",   "Audi 2 (Recliner)"     },
                { "5", "INOX South City Kolkata",        "South City Mall, Prince Anwar Shah Rd, Kolkata","Screen 1 (IMAX)",        "Screen 2 (4DX)"        },
                { "6", "Cinepolis Amanora Pune",         "Amanora Mall, Hadapsar, Pune",                  "Screen 1 (Dolby)",       "Screen 2 (VIP)"        },
                { "7", "PVR IMAX Ahmedabad",             "Alpha One Mall, SG Road, Ahmedabad",            "IMAX Screen",            "Audi 2 (4DX)"          },
                { "8", "Raj Mandir Cinema Jaipur",       "Bhagwan Das Rd, Jaipur",                        "Main Hall (Heritage)",   "Balcony (Premium)"     },
                { "9", "PVR Lulu Mall Kochi",            "LuLu Mall, NH 544, Edapally, Kochi",            "Screen 1 (Dolby Atmos)", "Screen 2 (4K)"         }
            };

            for (String[] td : theatreData) {
                int locIdx = Integer.parseInt(td[0]);
                String locationId = (locIdx < savedLocations.size()) ? savedLocations.get(locIdx).getId() : savedLocations.get(0).getId();

                Theatre theatre = theatreRepository.save(Theatre.builder()
                        .name(td[1])
                        .locationId(locationId)
                        .address(td[2])
                        .status("APPROVED")
                        .build());

                // Add 2 screens per theatre
                screenRepository.save(Screen.builder()
                        .theatreId(theatre.getId())
                        .name(td[3])
                        .seatLayoutId(savedLayout.getId())
                        .build());
                screenRepository.save(Screen.builder()
                        .theatreId(theatre.getId())
                        .name(td[4])
                        .seatLayoutId(savedLayout.getId())
                        .build());
            }
            System.out.println(">>> Seeded 10 APPROVED theatres with 2 screens each.");
        } else {
            System.out.println(">>> Theatres already seeded (" + theatreRepository.count() + "). Skipping.");
        }
    }
}
