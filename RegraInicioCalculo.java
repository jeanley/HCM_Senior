package custom.senior.apuracao;
import java.time.LocalDate;

import com.senior.ContextoGeralRH;
import com.senior.rule.Rule;

@Rule(description = "Regra Inicio de Calculo")
public class RegraInicioCalculo extends InicioCalculoColaborador {

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		ContextoInicioCalculoColaborador CxInicio = getContainer().getContextoInicioCalculoColaborador();
		ContextoGeralRH  CxGeral = getContainer().getContextoGeral();
		int CodFil = CxInicio.getColaborador().getCodigoFilial();
		
		
		
		int NumEmp = CxInicio.getColaborador().getNumEmp();
		int TipCol = CxInicio.getColaborador().getTipCol();
		int NumCad = CxInicio.getColaborador().getNumCad();
		LocalDate FimApu = CxInicio.getDataFinal();
		
		CodFil = CxGeral.getHistoricoFilial(NumEmp, TipCol, NumCad, FimApu).getCodFil();
		
		if(((NumEmp == 3) || (NumEmp == 4)) /*&& (NumCad != 30012486)*/) {
			if((CodFil == 4003) || (CodFil == 4023)) { //80%
				CxInicio.trocaDefinicaoSituacao(901,FimApu);
			}else
				if(CodFil == 3011) {
					//CxInicio.trocaDefinicaoSituacao(902);//100%
					CxInicio.trocaDefinicaoSituacao(301,FimApu);
				}else
					if((CodFil == 3119) || (CodFil == 3001) || (CodFil == 4001))
						CxInicio.trocaDefinicaoSituacao(308,FimApu); //50%
					else
						CxInicio.trocaDefinicaoSituacao(900, FimApu);
		}
			
		 

	}

}
