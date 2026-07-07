-- ============================
-- PARAMETROS GERAIS
-- ============================
-- Contabilização comparada com a ficha financeira
DECLARE @NUMEMP   INT  = 1;
DECLARE @DTINI    DATE = '2026-05-01';
DECLARE @DTFIM    DATE = '2026-05-31';

	SELECT T1.NUMEMP, T1.TIPCOL, T1.NUMCAD, T2.TIPCON, T2.CODFIL, F.NOMFIL, F.CODEST, T1.CODCAL, /*T1.CODEVE, T5.DESEVE, T5.TIPEVE,*/
ISNULL(t1.CODCLC,T7.CODCLC) AS CODCLC, T7.nomcon AS NOMECLC,  T6.CCURAT, ISNULL(T1.REFEVE,0) AS REFEVE, ISNULL(T1.VALEVE,0) AS VALEVE, ISNULL(T6.VALLAN,0) AS VALLAN
FROM 
(SELECT Z.NUMEMP,Z.TIPCOL,Z.NUMCAD,Z.CODCAL,Z.codrat,isnull(x.codclc,0) as codclc,SUM(Z.REFEVE) AS refeve,sum(Z.valeve) as valeve 
FROM r046ffr Z JOIN R008EVC X ON Z.codeve = X.codeve AND X.codtab = 100 AND X.tipeve IN (1,2,3)
WHERE Z.NUMEMP = @NUMEMP 
AND Z.CODCAL IN (
select CAL.CodCal from r044cal CAL
	WHERE CAL.NUMEMP = @NUMEMP 
	AND CAL.CODCAL IN (
    SELECT CAL2.CodCal 
    FROM r044cal CAL2 
    WHERE CAL2.numemp = @NUMEMP 
      AND CAL2.TipCal = 11 
      AND CAL2.PerRef = @DTINI 
      OR (CAL2.NumEmp = @NUMEMP 
          AND CAL2.TipCal IN (12,15) 
          AND CAL2.DatPag >= @DTINI 
          AND CAL2.DatPag <= @DTFIM)
))
group by NUMEMP,TIPCOL,NUMCAD,CODCAL,codrat,x.codclc)
T1
LEFT JOIN R034FUN T2 ON T2.NUMEMP = T1.NUMEMP AND T2.TIPCOL = T1.TIPCOL AND T2.NUMCAD = T1.NUMCAD
LEFT JOIN R044CAL T4 ON T4.NUMEMP = T1.NUMEMP AND T4.CODCAL = T1.CODCAL
LEFT JOIN R048CTB T6 ON T6.NUMEMP = T1.NUMEMP AND T6.TIPCOL = T1.TIPCOL AND T6.NUMCAD = T1.NUMCAD AND T6.CODCAL = T1.CODCAL AND T6.CODCLC = t1.CODCLC and t1.codrat = t6.codrat
left join r048clc T7 ON T7.codclc = T1.codclc
OUTER APPLY (              
    SELECT TOP (1) HSI.codfil , HSI.numemp             
    FROM r038hfi HSI WITH (NOLOCK)              
    WHERE HSI.NUMEMP = T2.NUMEMP              
      AND HSI.TIPCOL = T2.TIPCOL              
      AND HSI.NUMCAD = T2.NUMCAD              
      AND HSI.DATALT <= T6.datlan              
    ORDER BY HSI.DATALT DESC              
)  T3
LEFT JOIN R030FIL F WITH (NOLOCK)              
    ON F.NUMEMP = T3.NUMEMP AND F.CODFIL = T3.CODFIL 
WHERE T1.CODCAL IN (select CodCal from r044cal where numemp = @NUMEMP and TipCal = 11 and PerRef = @DTINI or (NumEmp = @NUMEMP and TipCal in (12,15) and DatPag >= @DTINI and DatPag <= @DTFIM))
