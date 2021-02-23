package com.fxg.demo.validation.annotation.group;

import javax.validation.GroupSequence;

@GroupSequence({New.class,Update.class})
public interface NewAndUpdate {
}
