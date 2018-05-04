CREATE FUNCTION [dbo].[EXCEL_NORMAL_DIST](@value       FLOAT,
                                     @mean        FLOAT,
                                     @sigma       FLOAT,
                                     @cummulative BIT)
  RETURNS NUMERIC(28,8)
AS
  /****************************************************************************************
  NAME:        udf_NORMDIST
  WRITTEN BY:  Tim Pickering
               https://social.msdn.microsoft.com/Forums/sqlserver/en-US/fa9fefd6-3e2d-4ff6-9757-2c56d51683b3/is-there-a-sql-server-2005-equivalant-fuction-to-excels-function-normdist?forum=transactsql
  DATE:        2010/07/13
  PURPOSE:     Mimics Excel's Function NORMDIST

               Usage:  SELECT dbo.udf_NORMDIST(.48321740,0,1,0)
                       OUTPUT: 0.35498205

  REVISION HISTORY

  Date                      Developer           Details
  2010/08/11                LC     Posted Function


  *****************************************************************************************/


  BEGIN

    DECLARE @x    FLOAT
    DECLARE @z    FLOAT
    DECLARE @t    FLOAT
    DECLARE @ans   FLOAT
    DECLARE @returnvalue FLOAT

    SELECT @x = (@value-@mean)/@sigma

    IF (@cummulative = 1)
      BEGIN

        SELECT @z   = abs(@x)/sqrt(2.0)
        SELECT @t   = 1.0/(1.0+0.5*@z)
        SELECT @ans = @t*exp(-@z*@z-1.26551223+@t*(1.00002368+@t*(0.37409196+@t*(0.09678418+@t*(-0.18628806+@t*(0.27886807+@t*(-1.13520398+@t*(1.48851587+@t*(-0.82215223+@t*0.17087277)))))))))/2.0

        IF (@x <= 0)
          SELECT @returnvalue = @ans
        ELSE
          SELECT @returnvalue = 1-@ans
      END
    ELSE
      BEGIN
        SELECT @returnvalue = exp(-@x*@x/2.0)/sqrt(2.0*3.14159265358979)
      END


    RETURN CAST(@returnvalue AS NUMERIC(28,8))


  END