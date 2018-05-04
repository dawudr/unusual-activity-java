CREATE PROCEDURE [dbo].[AddStockData]
    @symbol as varchar(20),
    @date as datetime2(7),
    @open as float,
    @close as float,
    @low as float,
    @high as float,
    @volume as bigint



AS
  BEGIN

    SET NOCOUNT ON;
    if not exists (Select 1 From stockdata where symbol = @symbol and [date] = @date)

      INSERT INTO [dbo].[stockdata]
      (symbol, [date],[open],[close],[high],[low],[volume],[date_part],time_part)
      Values(@symbol, @date, @open, @close, @high, @low, @volume, cast(@date as date), cast(@date as time))

    declare @ndvolume as FLOAT
    SET @ndvolume = (SELECT dbo.NORMAL_DIST_PDF(@volume,
                                                (SELECT AVG(volume) FROM [dbo].[stockdata] WHERE symbol = @symbol AND [date] BETWEEN DATEADD( DAY, -30, getdate()) AND getdate()),
                                                (SELECT STDEV(volume) FROM [dbo].[stockdata] WHERE symbol = @symbol AND [date] BETWEEN DATEADD( DAY, -30, getdate()) AND getdate())));

    declare @prange as FLOAT
    SET @prange = @high - @low;
    -- 		SET @ndprange = NORMAL_DIST_PDF(@prange,
    -- 												(SELECT AVG (prange) FROM stockdata WHERE symbol = @symbol AND [date] BETWEEN DATEADD( DAY, -30, getdate()) AND getdate()),
    -- 												(SELECT STDEV(prange) FROM stockdata WHERE symbol = @symbol AND [date] BETWEEN DATEADD( DAY, -30, getdate()) AND getdate()));

    INSERT INTO [dbo].[SymbolStats]
    (symbol, latestvolume, normaldist, [time_part], prange) VALUES(@symbol, @volume, @ndvolume, cast(@date as time), @prange);

  END
GO