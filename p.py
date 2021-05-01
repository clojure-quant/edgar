from secedgar.filings import Filing, FilingType

# 10Q filings for Apple (ticker "aapl")

my_filings = Filing(cik_lookup='aapl', 
                    filing_type=FilingType.FILING_10Q,
                    count=15)

my_filings.save('./data')
