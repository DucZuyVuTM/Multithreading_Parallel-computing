package task4;

/**
 * Задача обработки файла.
 * 
 * @param id   уникальный идентификатор (гарантирует ровно одну обработку)
 * @param type тип файла
 * @param size условный размер (10..100)
 */
public record FileTask(long id, FileType type, int size) { }
