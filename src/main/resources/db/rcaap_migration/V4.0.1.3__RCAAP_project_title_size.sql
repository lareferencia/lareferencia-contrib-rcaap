-- change size for project title field assigning more size from 255 to 500
ALTER TABLE project ALTER COLUMN title TYPE varchar(500);