SELECT 
	T1.NumEmp as Empresa, 
	T1.CodFil as CodigoFilial, 
	T2.NomFil as Filial, 
	T2.numcgc as CNPJ, 
	T1.TipCol as Tipo, 
	T1.NumCad as Marticula, 
	T1.NomFun as Nome, 
	T1.CodCar as CodigoCargo, 
	T3.TitCar as Cargo, 
	datediff(month, T4.DatAlt, getdate()) as TempoUltimoCargoEmMeses,
	datediff(month, T1.DatAdm, getdate()) as TempoDeCasaEmMeses,
	T1.TipSex as Sexo,
	format(T1.DatAdm,'dd/MM/yyyy') as DataAdmissao,
	case T1.SitAfa 
		WHEN 7 THEN format(T1.DatAfa,'dd/MM/yyyy')
	end as DataRescisao,
	T5.dessit as Situaçao,
	format(T1.ValSal, 'C', 'pt-BR') as Salario,
	T6.HorMes / 60 as CargaHorariaMes,
	T6.HorSem / 60 as CargaHorariaSemana,
	T1.CodVin as CodigoVinculo,
	T7.DesVin as Vinculo,
	T1.TipCon as CodigoTipoContrato,
	T8.ValKey as TipoContrato,
	T1.NumCpf as CPF,
	T26.CodSin as CodigoSindicato,
	T27.NomSin as Sindicato,
	T1.deffis as PossuiDeficiencia,
	T9.DesDef as TipoDeficiencia,
	T1.CotDef as PreencheCotaDeficiente,
	T1.DatNas as DataNascimento,
	T10.DesNac as Nacionalidade,
	T11.ValKey as ModoPagamento,
	T12.ValKey as TipoDeConta,
	T13.ValKey as PeriodoPagamento,
	T14.NomBan as Banco,
	T1.CodAge as Agencia,
	T1.ConBan as Conta,
	T1.DigBan as Digito,
	T1.NumPis as PIS,
	T15.NumCid as RG,
	T15.NumTel as Telefone,
	T15.EmaPar as EmailPerticular,
	T15.EmaCom as EmailComercial,
	T15.TipLgr as Logradouro,
	T15.EndRua as Endereço,
	T15.EndNum as Numero,
	T16.NomBai as Bairro,
	T16.CepBai as CEP,
	T17.NomCid as Cidade,
	T15.CodEst as Estado,
	T18.NomPai as Pais,
	T19.DesGra as Escolaridade,
	T20.DesEtn as RaçaCor,
	T21.ValKey as EstadoCivil,
	T1.PosTra as CodigoPostoTrabalho,
	T6.nomEsc as Escala,
	T22.ValKey as Turma,
	T1.CodEst as Estabilidade,
	T23.FimEtb as DataEstabilidade,
	T24.NomLoc as Local,
	T1.CodCcu as CodCCusto,
	T30.NomCcu as CCusto,
	T25.Valkey as ControlaPonto,
	T15.DurCon + T15.ProCon as DuracaoContrato,
	format(T1.DatAdm + T15.DurCon + T15.ProCon,'dd/MM/yyyy') as DataTerminoContrato,
	case T29.CodSel 
		WHEN 5 THEN 'S'
	end as OposAssistencial,
	--T32.NomDep as NomeMae,
	--T33.NomDep as NomPai,
	format(T1.DatAfa,'dd/MM/yyyy') as DataUltimoAfastamento,
	format(T28.DatTer,'dd/MM/yyyy') as DataRetornoUltimoAfastamento,
	T1.USU_MatAdp as MatriculaLegado,
	T31.NomCid as CidadeNascimento,
	T31.CodEst as EstadoNascimento
from r034fun T1
LEFT JOIN R030FIL (nolock) T2 on T2.NumEmp = T1.NumEmp and T2.CodFil = T1.CodFil
LEFT JOIN R024CAR (nolock) T3 on T3.EstCar = T1.EstCar and T3.CodCar = T1.CodCar
LEFT JOIN(SELECT NumEmp, TipCol, NumCad, EstCar, CodCar, MAX(DATALT) as DatAlt FROM R038HCA GROUP BY NumEmp, TipCol, NumCad, EstCar, CodCar) T4 on T4.NumEmp = T1.NumEmp and T4.TipCol = T1.TipCol and T4.NumCad = T1.NumCad and T4.EstCar = T1.EstCar and T4.CodCar = T1.CodCar
LEFT JOIN R010SIT (nolock) T5 on T5.codsit = T1.sitafa
LEFT JOIN R006ESC (nolock) T6 on T6.CodEsc = T1.CodEsc
LEFT JOIN R022VIN (nolock) T7 on T7.CodVin = T1.CodVin
LEFT JOIN R996LSF (nolock) T8 on T8.KeyNam = T1.TipCon and T8.LstNam = 'LTipCon'
LEFT JOIN R022DEF (nolock) T9 on T9.CodDef = T1.CodDef
LEFT JOIN R023NAC (nolock) T10 on T10.CodNac = T1.CodNac
LEFT JOIN R996LSF (nolock) T11 on T11.KeyNam = T1.ModPag and T11.LstNam = 'LModPag'
LEFT JOIN R996LSF (nolock) T12 on T12.KeyNam = T1.TpcTba and T12.LstNam = 'LTpcTba'
LEFT JOIN R996LSF (nolock) T13 on T13.KeyNam = T1.PerPag and T13.LstNam = 'LPerPag'
LEFT JOIN R012BAN (nolock) T14 on T14.CodBan = T1.CodBan
LEFT JOIN R034CPL (nolock) T15 on T15.NumEmp = T1.NumEmp and T15.TipCol = T1.TipCol and T15.NumCad = T1.NumCad
LEFT JOIN R074BAI (nolock) T16 on T16.CodCid = T15.CodCid and T16.CodBai = T15.CodBai
LEFT JOIN R074CID (nolock) T17 on T17.CodCid = T15.CodCid
LEFT JOIN R074PAI (nolock) T18 on T18.CodPai = T15.CodPai
LEFT JOIN R022GRA (nolock) T19 on T19.grains = T1.GraIns
LEFT JOIN R010ETN (nolock) T20 on T20.CodEtn = T1.RacCor
LEFT JOIN R996LSF (nolock) T21 on T21.KeyNam = T1.EstCiv and T21.LstNam = 'LEstCiv'
LEFT JOIN R996LSF (nolock) T22 on T22.KeyNam = T6.TurEsc and T22.LstNam = 'LTipTur'
LEFT JOIN(SELECT NumEmp, TipCol, NumCad, MAX(FIMETB) as FimEtb FROM R038HEB GROUP BY NumEmp, TipCol, NumCad) T23 on T23.NumEmp = T1.NumEmp and T23.TipCol = T1.TipCol and T23.NumCad = T1.NumCad
LEFT JOIN R016ORN (nolock) T24 on T24.TabOrg = T1.TabOrg and T24.NumLoc = T1.NumLoc
LEFT JOIN R996LSF (nolock) T25 on T25.KeyNam = T1.ConRho and T25.LstNam = 'LConRho'
LEFT JOIN R038HSI (nolock) T26 on T26.NumEmp = T1.NumEmp and T26.TipCol = T1.TipCol and T26.NumCad = T1.NumCad and T26.DatAlt = (select MAX(DatAlt) from r038hsi where numemp = T1.NumEmp and tipcol = T1.TipCol and numcad = T1.NumCad)
LEFT JOIN R014SIN (nolock) T27 on T27.CodSin = T26.CodSin
LEFT JOIN R038AFA (nolock) T28 on T28.NumEmp = T1.NumEmp and T28.TipCol = T1.TipCol and T28.NumCad = T1.NumCad and T28.DatAfa = T1.DatAfa
LEFT JOIN R034SEL (nolock) T29 on T29.NumEmp = T1.NumEmp and T29.TipCol = T1.TipCol and T29.NumCad = T1.NumCad and T29.CodSel = 5
LEFT JOIN R018CCU (nolock) T30 on T30.NumEmp = T1.NumEmp and T30.CodCcu = T1.CodCcu
LEFT JOIN R074CID (nolock) T31 on T31.CodCid = T15.CciNas
--LEFT JOIN R036DEP (nolock) T32 on T32.NumEmp = T1.NumEmp and T32.TipCol = T1.TipCol and T32.NumCad = T1.NumCad and T32.GraPar = 3 and T32.TipSex = 'F'-- Verificar cadastro de dependentes 
--LEFT JOIN R036DEP (nolock) T27 on T27.NumEmp = T1.NumEmp and T27.TipCol = T1.TipCol and T27.NumCad = T1.NumCad and T26.GraPar = 3 and T26.TipSex = 'M' Verificar cadastro de dependentes 
--where T1.NumEmp in (3,4)
--where T1.NumCad = 20004612
