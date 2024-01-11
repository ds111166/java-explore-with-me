package ru.practicum.ewm.comment.data;

public enum StateComment {
    PENDING,
    PUBLISHED,
    CANCELED_INTOLERANCE,   //нетерпимость
    CANCELED_RUDENESS,      //грубость
    CANCELED_EXTREMISM,     //экстремизм
    CANCELED_SPAM,          //спам
    CANCELED_PORNOGRAPHY,   //порнография
    CANCELED_PROHIBITION    //запрет
}
