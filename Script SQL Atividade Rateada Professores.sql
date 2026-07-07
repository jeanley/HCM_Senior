select 
	apf.numemp, 
	case 
		when apf.numemp = 1 then 'ABEC'
		when apf.numemp = 2 then 'SOME'
		when apf.numemp = 3 then 'UBEE'
		when apf.numemp = 4 then 'UNBEC'
	end as Mantenedora,
	apf.numcad,
	fun.nomfun,
	fun.codcar,
	car.titcar,
	fun.codfil,
	fil.nomfil, 
	fil.codest,
	fun.sitafa,
	sit.dessit,
	fun.datadm,
	apf.codapf as codatividade,
	ati.desapf as atividade,
	apf.datini,
	apf.datfim,
	apf.hormes / 60 as HorasMes,
	apf.hormes as minutos,
	apf.valapf,
	apf.codrat,
	rat.desrat as nomccu,
	apf.estsal,
	apf.clasal,
	apf.nivsal, 
	fun.usu_matadp as matriculaLegado,
	fun.usu_filmat as FilialLegado, 
	CASE
		WHEN fun.sitafa = 7 THEN fun.datafa
	END AS datdem
from r171apf apf
left join r034fun (nolock) fun on fun.numemp = apf.numemp and fun.tipcol = apf.tipcol and fun.numcad = apf.numcad
left join r024car (nolock) car on car.estcar = fun.estcar and car.codcar = fun.codcar
left join r030fil (nolock) fil on fil.numemp = fun.numemp and fil.codfil = fun.codfil
left join r010sit (nolock) sit on sit.codsit = fun.sitafa
left join r171ati (nolock) ati on ati.codapf = apf.codapf
left join r020rat (nolock) rat on rat.codrat = apf.codrat
