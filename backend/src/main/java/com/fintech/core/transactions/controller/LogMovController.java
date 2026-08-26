package com.fintech.core.transactions.controller;


import com.fintech.core.transactions.dto.ReversalRequestDTO;
import com.fintech.core.transactions.dto.TransactionRequestDTO;
import com.fintech.core.transactions.dto.TransactionResponseDTO;
import com.fintech.core.transactions.dto.TransferRequestDTO;
import com.fintech.core.transactions.service.LogMovService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class LogMovController {

    private final LogMovService logMovService;

    /**
     * Endpoint para registrar Depósitos, Retiros y otras transacciones.
     * @param dto Datos de la transacción (validado con Bean Validation)
     */
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @Valid @RequestBody TransactionRequestDTO dto,
            HttpServletRequest request) {
        // Lógica para obtener la IP real (considerando Proxies o Balanceadores)
        String remoteAddr = request.getHeader("X-Forwarded-For");
        if (remoteAddr == null || remoteAddr.isEmpty()) {
            remoteAddr = request.getRemoteAddr();
        }
        // Si el DTO es un record, creamos uno nuevo con la IP (ya que son inmutables)
        TransactionRequestDTO dtoWithIp = new TransactionRequestDTO(
                dto.accountId(),
                dto.originId(),
                dto.amount(),
                dto.description(),
                dto.transactionReference(),
                dto.createdBy(),
                remoteAddr,  // Aquí inyectamos la IP capturada
                dto.configId(),
                dto.originCode()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(logMovService.processTransaction(dtoWithIp));
    }

    /**
     * Endpoint para procesar el reverso de una transacción.
     * @param dto Contiene el ID original y el motivo del reverso.
     */
    @PostMapping("/reversal")
    public ResponseEntity<TransactionResponseDTO> reverseTransaction(
            @Valid @RequestBody ReversalRequestDTO dto) {
        return ResponseEntity.ok(logMovService.reverseTransaction(dto));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(@Valid @RequestBody TransferRequestDTO dto) {
        return ResponseEntity.ok(logMovService.processTransfer(dto));
    }


}
