# CSV format

The project has functionality of reading csv data and establishing the initial state of the hotel by doing so.

## Headers

The csv file can't containg any headings (no column names). Only the values are allowed.

## Separator

The csv uses semicolon `;` as a values separator and new line `\n` as a line separator.

## Columns

The input csv has to contain the following columns:

| Column name | Description |
|-------------|-------------|
| Room number | 3-digit code of a room number. Only 0-9 characters are allowed e.g. `001`, `532`|
| Room description | Short description of a room | 
| Price per night | A positive number with two decimal places  e.g. `1.01` |
| Capacity | A whole positive number |
| Guests | Names of all guests in the room separated by pipes `\|` e.g. `John Doe\|Marry Hoppins`. The pipe MUST be between two names. The following is invalid: `;John Doe\|;` |
| Checkin date | Date in the format `DD-MM-YYYY` |
| Planned Stay | A whole positive number, which represents the number of nights planned to stay in the hotel |
| Remarks | Guests note about additional requirements for the hotel room. |

## Example

Check out the [example csv file](./csv_format.csv)
