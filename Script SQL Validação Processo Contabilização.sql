-- ============================
-- PARAMETROS GERAIS
-- ============================
DECLARE @NUMEMP   INT  = 4;
DECLARE @DTINI    DATE = '2025-12-01';
DECLARE @DTFIM    DATE = '2025-12-31';
 
---------------------------------------------------
-- ANALISE OS CÓDIGOS DE CÁLCULO DO MÊS
---------------------------------------------------
SELECT t4.*, t1.*
FROM R044CAL T1
LEFT JOIN r996lsf T4 WITH (NOLOCK)
       ON T4.keynam = T1.TIPCAL
      AND T4.lstnam = 'LTipCal'
WHERE T1.NUMEMP = @NUMEMP
  AND T1.perref = @DTINI
  AND T1.TIPCAL NOT IN (15, 91);
 
---------------------------------------------------
-- CALCULOS COMPLEMENTARES (TIPCAL 15)
---------------------------------------------------
SELECT t4.*, t1.*
FROM R044CAL T1
LEFT JOIN r996lsf T4 WITH (NOLOCK)
       ON T4.keynam = T1.TIPCAL
      AND T4.lstnam = 'LTipCal'
JOIN (
        SELECT DISTINCT NUMEMP, CODCAL
        FROM r046ver
     ) T5
       ON T5.NUMEMP = T1.NUMEMP
      AND T5.CODCAL = T1.CODCAL
WHERE T1.NUMEMP = @NUMEMP
  AND T1.DATPAG BETWEEN @DTINI AND @DTFIM
  AND T1.TIPCAL = 15;
 
---------------------------------------------------
-- ANALISE CALCULOS ESPECIFICOS
---------------------------------------------------
--SELECT t4.*, t1.*
--FROM R044CAL T1
--LEFT JOIN r996lsf T4 WITH (NOLOCK)
--       ON T4.keynam = T1.TIPCAL
--      AND T4.lstnam = 'LTipCal'
--WHERE T1.NUMEMP = @NUMEMP
--  AND T1.CODCAL IN (10938, 10953, 10933);
 
---------------------------------------------------
-- ANALISE RESCISAO COMPLEMENTAR
---------------------------------------------------
--SELECT *
--FROM R042RCS
--WHERE NUMEMP = @NUMEMP
--  AND DATPAG BETWEEN @DTINI AND @DTFIM;
 
---------------------------------------------------
-- ANALISE CLCS DO LOTE DOS CALCULOS COMPLEMENTARES
---------------------------------------------------
--SELECT DISTINCT codclc, cplhis, concre, condeb
--FROM R048CTB
--WHERE NUMEMP = @NUMEMP
--  AND DATLAN = @DTINI
--  AND CODCLC IN (
--        SELECT DISTINCT T2.codclc
--        FROM r046ver T1
--        JOIN R008EVC T2
--          ON T1.CODEVE = T2.codeve
--         AND T2.codtab = 100
--        WHERE T1.NUMEMP = @NUMEMP
--          AND T1.CODCAL IN (10939, 10940, 10941, 10943, 10944)
--  );
 
---------------------------------------------------
-- FILTRA CODIGO DE CALCULOS DO LOTE CONTABIL
---------------------------------------------------
SELECT DISTINCT T1.NUMEMP, T1.CODCAL
FROM R048CTB T1
LEFT JOIN R030FIL T2 ON T1.CODFIL = T2.CODFIL
LEFT JOIN R034FUN T3 ON T3.NUMEMP = T1.NUMEMP
                    AND T3.TIPCOL = T1.TIPCOL
                    AND T3.NUMCAD = T1.NUMCAD
LEFT JOIN R018CCU T4 ON T4.NUMEMP = T1.NUMEMP
                    AND T4.CODCCU = T1.CODCCU
WHERE T1.NUMEMP = @NUMEMP
  AND T1.DATLAN BETWEEN @DTINI AND @DTFIM;
 
---------------------------------------------------
-- ANALITICO DO LOTE CONTABIL
---------------------------------------------------
--SELECT DISTINCT 
--       T1.NUMEMP,
--       T1.NUMCAD,
--       T1.DATLAN,
--       T1.CODFIL,
--       T2.NOMFIL,
--       T1.CONCRE,
--       T1.VALLAN,
--       T1.CODCAL,
--       T2.CODEST,
--       T1.CODCLC,
--       T1.CPLHIS,
--       T1.VALLAN AS VALOR,
--       T1.CONDEB,
--       T1.CONCRE
--FROM R048CTB T1
--LEFT JOIN R030FIL T2 ON T1.CODFIL = T2.CODFIL
--LEFT JOIN R034FUN T3 ON T3.NUMEMP = T1.NUMEMP
--                    AND T3.TIPCOL = T1.TIPCOL
--                    AND T3.NUMCAD = T1.NUMCAD
--LEFT JOIN R018CCU T4 ON T4.NUMEMP = T1.NUMEMP
--                    AND T4.CODCCU = T1.CODCCU
--WHERE T1.NUMEMP = @NUMEMP
--  AND T1.DATLAN BETWEEN @DTINI AND @DTFIM
--ORDER BY 1, 3, 6;
