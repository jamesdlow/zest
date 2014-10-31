DELIMITER //

CREATE TABLE zest_songs (
	song_date INT UNSIGNED NOT NULL DEFAULT 0,
	song_user VARCHAR(32) NOT NULL DEFAULT '',
	song_other VARCHAR(32) NOT NULL DEFAULT '',
	song_title VARCHAR(1024) NOT NULL DEFAULT ''
);//

DROP PROCEDURE IF EXISTS zest_insert;//
CREATE PROCEDURE zest_insert(
	IN new_song_date INT UNSIGNED,
	IN new_song_user VARCHAR(32),
	IN new_song_other VARCHAR(32),
	IN new_song_title VARCHAR(1024)
)
BEGIN
	INSERT INTO zest_songs (song_date,song_user,song_other,song_title)
		VALUES (new_song_date,new_song_user,new_song_other,new_song_title);
END;//

DROP PROCEDURE IF EXISTS zest_get_date;//
CREATE PROCEDURE zest_get_date(
	IN days INT UNSIGNED
)
BEGIN
	DECLARE date_min INT UNSIGNED;
	SET date_min = UNIX_TIMESTAMP() - days*24*60*60;
	SELECT MAX(song_date), song_title, COUNT(song_date) FROM zest_songs
		WHERE song_date >= date_min GROUP BY song_title;
END;//
