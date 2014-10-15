/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.nosov.SMSreader.exceptions.errors;

/**
 * Перечень и расшифровка возмоных ошибок при работе с БД.
 * @author Носов А.В.
 */
public enum ErrorsDB {
    
    // Variables declaration
    /** Ошибка парсера JSON. */
    ERROR_JSON(0, "ERROR_JSON"),
    /** Не обрабатываемый тип сообщения. */
    WARNING_UNPROCESSED_TYPE(1, "WARNING_UNPROCESSED_TYPE"),
    /** Неизвестный тип сообщения. */
    WARNING_UNKNOWN_TYPE(2, "WARNING_UNKNOWN_TYPE"),
    /** Слишком много данных для отображения векторной диаграммы. */
    WARNING_VECTOR_DIAGRAM_DATA(3, "WARNING_VECTOR_DIAGRAM_DATA"),
    /** Несовпадение идентификаторов. */
    ERROR_MODULE_ID(4, "ERROR_MODULE_ID"),
    /** Неверная команда запроса. */
    ERROR_DATA_REQUEST(5, "ERROR_DATA_REQUEST"),
    /** Неверная команда ответа. */
    ERROR_DATA_RESPONSE(6, "ERROR_DATA_RESPONSE"),
    /** Неверный тип запроса. */
    ERROR_TYPE_REQUEST(5, "ERROR_TYPE_REQUEST"),
    /** Неверный тип ответа. */
    ERROR_TYPE_RESPONSE(6, "ERROR_TYPE_RESPONSE");
    
    /** Описание ошибки. */
    private final String description;
    /** Код ошибки. */
    private final int code;
    // End of variables declaration

    /**
     * Инициализация ошибки.
     * @param description описание ошибки
     */
    ErrorsDB(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Возвращает описание ошибки.
     * @return описание ошибки
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Возвращает код ошибки.
     * @return код ошибки
     */
    public int getCode() {
        return code;
    }
}
