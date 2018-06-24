/*
private boolean success;
    private String message;
    private List<EventDto> events;
    private List<EventDto> recommended; */

export class Holder<T>{
    success: boolean;
    message: String;
    result: [T];
    recommendedResults: [T];
}