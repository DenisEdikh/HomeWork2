package aston.mapper;

import aston.dto.FilmDto;
import aston.model.Film;

import java.util.List;

public class FilmMapper {
    public static FilmDto toFilmDto(Film film) {
        FilmDto filmDto = new FilmDto();

        filmDto.setId(film.getId());
        filmDto.setTitle(film.getTitle());
        return filmDto;
    }

    public static List<FilmDto> toFilmDto(List<Film> films) {
        return films.stream().map(FilmMapper::toFilmDto).toList();
    }
}
