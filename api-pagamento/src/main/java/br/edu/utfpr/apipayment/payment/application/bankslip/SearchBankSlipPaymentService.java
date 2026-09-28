package br.edu.utfpr.apipayment.payment.application.bankslip;

import br.edu.utfpr.apipayment.payment.api.bankslip.response.BankSlipPaymentResponse;
import br.edu.utfpr.apipayment.payment.api.pix.response.PixPaymentResponse;
import br.edu.utfpr.apipayment.payment.domain.BankSlip;
import br.edu.utfpr.apipayment.payment.domain.Pix;
import br.edu.utfpr.apipayment.payment.infrastructure.mapper.BankSlipPaymentMapper;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.BankSlipRepository;
import br.edu.utfpr.apipayment.payment.infrastructure.persistence.PaymentRepository;
import br.edu.utfpr.apipayment.shared.exception.ResourceNotFoundException;
import br.edu.utfpr.apipayment.shared.response.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchBankSlipPaymentService {

    private static BankSlipRepository bankSlipRepository;
    private static BankSlipPaymentMapper bankSlipPaymentMapper;

    public SearchBankSlipPaymentService(
            BankSlipRepository bankSlipRepository,
            BankSlipPaymentMapper bankSlipPaymentMapper
    ){
        this.bankSlipRepository = bankSlipRepository;
        this.bankSlipPaymentMapper = bankSlipPaymentMapper;
    }


    @Transactional(readOnly = true)
    public BankSlipPaymentResponse findById(Long id){
        BankSlip pixPayment = bankSlipRepository.findByPaymentId(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PIX_NOT_FOUND", "Boleto não encontrado: " + id
                ));

        return bankSlipPaymentMapper.toResponse(pixPayment);
    }

    @Transactional(readOnly = true)
    public PageResponse<BankSlipPaymentResponse> findAll(Pageable pageable) {
        return PageResponse.of(bankSlipRepository.findAll(pageable).map(bankSlipPaymentMapper::toResponse));
    }
}
