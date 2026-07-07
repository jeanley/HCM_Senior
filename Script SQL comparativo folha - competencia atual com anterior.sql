select	a.numemp,
		e.codfil,
		h.nomfil,
	 	a.numcad,
		e.nomfun,
		CONVERT(VARCHAR(10), e.datadm, 103) as datadm,
		g.titred as cargo,
		f.dessit,
		a.perref as competencia,
		COALESCE(a.total, 0) as proventosOutubro,
		COALESCE(b.total, 0) as descontosOutubro,
	    (COALESCE(a.total, 0) - COALESCE(b.total, 0)) as liquidoOutubro,
		COALESCE(c.total, 0) as proventosSetembro,
		COALESCE(d.total, 0) as descontosSetembro,
		(COALESCE(c.total, 0) - COALESCE(d.total, 0)) as liquidoSetembro
from	usu_vProventos a
        left join usu_vDescontos b on b.numemp = a.numemp and b.tipcol = a.tipcol and b.numcad = a.numcad and b.codcal = a.codcal
		left join usu_vProventos c on c.numemp = a.numemp and c.tipcol = a.tipcol and c.numcad = a.numcad and c.codcal = 10932
		left join usu_vDescontos d on d.numemp = a.numemp and d.tipcol = a.tipcol and d.numcad = a.numcad and d.codcal = 10932
		inner join r034fun e on e.numemp = a.numemp and e.tipcol = a.tipcol and e.numcad = a.numcad
		inner join r010sit f on f.codsit = e.sitafa
		inner join r024car g on g.estcar = e.estcar and g.codcar = e.codcar
		inner join r030fil h on h.numemp = e.numemp and h.codfil = e.codfil
where	(a.numemp = 1 and a.codcal = 10938)
UNION
select	a.numemp,
		e.codfil,
		h.nomfil,
	 	a.numcad,
		e.nomfun,
		CONVERT(VARCHAR(10), e.datadm, 103) as datadm,
		g.titred as cargo,
		f.dessit,
		a.perref as competencia,
		COALESCE(a.total, 0) as proventosOutubro,
		COALESCE(b.total, 0) as descontosOutubro,
	    (COALESCE(a.total, 0) - COALESCE(b.total, 0)) as liquidoOutubro,
		COALESCE(c.total, 0) as proventosSetembro,
		COALESCE(d.total, 0) as descontosSetembro,
		(COALESCE(c.total, 0) - COALESCE(d.total, 0)) as liquidoSetembro
from	usu_vProventos a
        left join usu_vDescontos b on b.numemp = a.numemp and b.tipcol = a.tipcol and b.numcad = a.numcad and b.codcal = a.codcal
		left join usu_vProventos c on c.numemp = a.numemp and c.tipcol = a.tipcol and c.numcad = a.numcad and c.codcal = 21329
		left join usu_vDescontos d on d.numemp = a.numemp and d.tipcol = a.tipcol and d.numcad = a.numcad and d.codcal = 21329
		inner join r034fun e on e.numemp = a.numemp and e.tipcol = a.tipcol and e.numcad = a.numcad
		inner join r010sit f on f.codsit = e.sitafa
		inner join r024car g on g.estcar = e.estcar and g.codcar = e.codcar
		inner join r030fil h on h.numemp = e.numemp and h.codfil = e.codfil
where	(a.numemp = 2 and a.codcal = 21334)
UNION
select	a.numemp,
		e.codfil,
		h.nomfil,
	 	a.numcad,
		e.nomfun,
		CONVERT(VARCHAR(10), e.datadm, 103) as datadm,
		g.titred as cargo,
		f.dessit,
		a.perref as competencia,
		COALESCE(a.total, 0) as proventosOutubro,
		COALESCE(b.total, 0) as descontosOutubro,
	    (COALESCE(a.total, 0) - COALESCE(b.total, 0)) as liquidoOutubro,
		COALESCE(c.total, 0) as proventosSetembro,
		COALESCE(d.total, 0) as descontosSetembro,
		(COALESCE(c.total, 0) - COALESCE(d.total, 0)) as liquidoSetembro
from	usu_vProventos a
        left join usu_vDescontos b on b.numemp = a.numemp and b.tipcol = a.tipcol and b.numcad = a.numcad and b.codcal = a.codcal
		left join usu_vProventos c on c.numemp = a.numemp and c.tipcol = a.tipcol and c.numcad = a.numcad and c.codcal = 463
		left join usu_vDescontos d on d.numemp = a.numemp and d.tipcol = a.tipcol and d.numcad = a.numcad and d.codcal = 463
		inner join r034fun e on e.numemp = a.numemp and e.tipcol = a.tipcol and e.numcad = a.numcad
		inner join r010sit f on f.codsit = e.sitafa
		inner join r024car g on g.estcar = e.estcar and g.codcar = e.codcar
		inner join r030fil h on h.numemp = e.numemp and h.codfil = e.codfil
where	(a.numemp = 3 and a.codcal = 468)
and     a.numcad = 30006030
UNION
select	a.numemp,
		e.codfil,
		h.nomfil,
	 	a.numcad,
		e.nomfun,
		CONVERT(VARCHAR(10), e.datadm, 103) as datadm,
		g.titred as cargo,
		f.dessit,
		a.perref as competencia,
		COALESCE(a.total, 0) as proventosOutubro,
		COALESCE(b.total, 0) as descontosOutubro,
	    (COALESCE(a.total, 0) - COALESCE(b.total, 0)) as liquidoOutubro,
		COALESCE(c.total, 0) as proventosSetembro,
		COALESCE(d.total, 0) as descontosSetembro,
		(COALESCE(c.total, 0) - COALESCE(d.total, 0)) as liquidoSetembro
from	usu_vProventos a
        left join usu_vDescontos b on b.numemp = a.numemp and b.tipcol = a.tipcol and b.numcad = a.numcad and b.codcal = a.codcal
		left join usu_vProventos c on c.numemp = a.numemp and c.tipcol = a.tipcol and c.numcad = a.numcad and c.codcal = 411
		left join usu_vDescontos d on d.numemp = a.numemp and d.tipcol = a.tipcol and d.numcad = a.numcad and d.codcal = 411
		inner join r034fun e on e.numemp = a.numemp and e.tipcol = a.tipcol and e.numcad = a.numcad
		inner join r010sit f on f.codsit = e.sitafa
		inner join r024car g on g.estcar = e.estcar and g.codcar = e.codcar
		inner join r030fil h on h.numemp = e.numemp and h.codfil = e.codfil
where	(a.numemp = 4 and a.codcal = 417)

--18689
